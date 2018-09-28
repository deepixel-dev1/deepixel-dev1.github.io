#pragma once

#include <opencv2/opencv.hpp>

/**
 *@mainpage ARing_API Doxygen
 *@brief ARing API는 영상처리 기반으로 얼굴의 귀 위치정보를 추정하여, 제공하는 API 모듈입니다.
 *@version 1.0v
 *@author HooHyun_Kim, Honam_Ahn
 *@date 2018/09/14
 */

namespace dp {
    namespace aringnative {

        /**
         *@brief ARing API 결과저장 구조체
         *@details ARing API의 DetectFace 함수를 통해 추정된 귀에 대한 정보를 저장한다.
         */
        /**
         *@brief Result of face detection
         *@details This class contains information about the results of face detection.
         */
        struct DPARingResult {
            bool detected;             /** 검출여부 */
            cv::Point leftEarringPos;  /** 왼쪽 귀의 위치 */
            cv::Point rightEarringPos; /** 오른쪽 귀의 위치 */
            cv::Mat mask;              /** 얼굴 마스크 */
            float earringScaleYLeft;   /** 왼쪽 귀걸이 스케일 */
            float earringScaleYRight;  /** 오른쪽 귀걸이 스케일 */
            float rotX;                /** x방향의 얼굴 각도 */
            float rotY;                /** y방향의 얼굴 각도 */

            DPARingResult() {};

            virtual ~DPARingResult() {};
        };

        /**
         *@brief 영상을 회전 시킬 각도
         *@details 영상을 회전 시킬 각도를 나타낸다. 90의 배수로만 회전 가능하다.
         */
        enum DP_IMAGE_ROTATION {
            CW_0 = 0,     /** 회전하지 않는다. */
            CW_90 = 90,   /** 시계방향으로 90도 */
            CW_180 = 180, /** 시계방향으로 180도 */
            CW_270 = 270, /** 시계방향으로 270도 */
        };

        /**
         *@brief 영상의 타입
         *@details 영상의 타입을 나타낸다. 안드로이드에서는 YUV_420_888을 iOS에서는 BGRA_8888 타입을 사용하는것을 권장한다.
         */
        enum DP_IMAGE_TYPE {
            YUV_420_888,
            BGRA_8888,
        };

        /**
         *@brief ARing API 컨트롤 인터페이스
         *@details IARing은 ARing API의 모든 라이브러리를 컨트롤 할 수 있는 인터페이스 입니다. 사용자는 initialize 함수를 통해 ARing API를 초기화 할 수 있고, DetectFace함수를 통해 ARing API의 기능을 실행 할 수 있다.
         *@code
         *
         * <영상변환 및 ARing API 실행>
         *
         *    cv::Mat matProcessing;
         *    cv::cvtColor(matSRC, matProcessing, CV_YUV2GRAY_NV21); //(CV_YUV2GRAY_NV21) YUV øµªÛ¿ª gray scale øµªÛ¿∏∑Œ ∫Ø∞Ê«œ¥¬ conversion code
         *    dp::aringnative::DPAringResult result = g_ptrARing->DetectFace(matProcessing);
         *
         * <귀걸이 영상입력 및 출력함수 호출>
         *
         *    cv::Mat earingPic = cv::imread("earingPic.png"); //귀걸이 영상 입력
         *    if(result.detected)
         *    {
         *        drawEarring(matDST, earingPic, result, true); // LEFT
         *        drawEarring(matDST, earingPic, result, false); // RIGHT
         *    }
         *
         * <귀걸이 출력함수 구현>
         *
         *    void drawEarring(cv::Mat dst, cv::Mat earingPic, DPARingResult result, bool isLeft)
         *    {
         *        float fEaring_scale_y = isLeft ? result.earringScaleYLeft : earringScaleYRight;
         *        cv::Mat matEarring;
         *        cv::resize(earingPic, matEarring, cv::Size(0, 0), fEaring_scale_y, fEaring_scale_y);
         *          if(matEarring.empty())
         *            return;
         *        cv::Point earingPt = isLeft ? result.leftEarringPos : result.rightEarringPos;
         *        cv::Rect earingRect(0, 0, matEarring.cols, matEarring.rows);
         *        earingRect = earingRect + earingPt;
         *        earingRect = earingRect - cv::Point(earingRect.width / 2, earingRect.width / 4);
         *        cv::Rect safeRect(0, 0, dst.cols, dst.rows);
         *        earingRect = earingRect & safeRect;
         *        if(matEarring.size().area()!=earingRect.area())
         *            return;
         *        // 얼굴을 일정 각도 이상 돌리면 mask를 적용하지 않는다.
         *        if (isLeft) {
         *            if (!(result.rotX < -15))
         *                matEarring.setTo(0, result.mask(earingRect));
         *        } else {
         *            if (!(result.rotX > 15))
         *                matEarring.setTo(0, result.mask(earingRect));
         *        }
         *        cv::Mat earingMask;
         *        cv::compare(matEarring, 0, earingMask, cv::CMP_GT);
         *          // dst의 타입(디바이스에서 영상을 출력할 때 사용하는 타입)으로 변경한다.
         *        cv::cvtColor(earingMask, earingMask, CV_BGR2RGBA);
         *        cv::cvtColor(matEarring, matEarring, CV_BGR2RGBA);
         *        cv::subtract(dst(earingRect), earingMask, dst(earingRect));
         *        cv::add(matEarring, dst(earingRect), dst(earingRect));
         *    }
         *@endcode
         */
        struct IARing {

            IARing() {};

            virtual ~IARing() {};

            /*!
             * @brief 초기화 함수
             * @param[in] imgWidth 이미지의 가로 크기
             * @param[in] imgHeight 이미지의 세로 크기
             * @param[in] imageRotation 이미지를 회전할 각도
             * @param[in] sensorWidth 카메라 센서의 가로 크기
             * @param[in] sensorHeight 카메라 센서의 세로 크기
             * @param[in] focalLength 카메라 포커스 값
             * @exception dp::exception::DPException "Already initilalized"
             *            > 두번 이상 초기화를 했을 경우 발생한다.
             * @exception dp::exception::DPException "initilalized Failed"
             *            > 예상하지 못한 에러에 의해 초기화가 완료되지 않을 경우 발생한다.
             */
            virtual void initialize(const int imgWidth, const int imgHeight,
                                    const DP_IMAGE_ROTATION imageRotation = CW_90,
                                    const float sensorWidth = 0.0f, const float sensorHeight = 0.0f,
                                    const float focalLength = 0.0f) = 0;

            /*!
             * @brief ARing API 동작(실행) 함수
             * @details 해당 함수를 통해 귀의 위치정보를 추정한다.
             * @param[in] src 입력영상
             *            > 사이즈는 320 X 240보다 커야 한다.
             * @param[in] imageType 입력영상의 타입
             * @param[out] DPARingResult 결과출력
             *            > 변환 값은 얼굴 검출 유무 및 귀의 위치 정보 등이 포함되어 있다.
             * @exception dp::exception::DPException "There is no image"
             *            > 입력 영상이 비어있을 경우 발생한다.
             * @exception dp::exception::DPException "No initialized"
             *            > 초기화를 하지 않은 경우 발생한다.
             * @exception dp::exception::DPException "Image size is different from the initial image size"
             *            > 초기화할 때 입력한 영상의 크기와 현재 입력 영상의 크기가 다른 경우 발생한다.
             */
            virtual DPARingResult DetectFace(const cv::Mat src,
                                             const DP_IMAGE_TYPE imageType = YUV_420_888) = 0;
        };
    }
}
