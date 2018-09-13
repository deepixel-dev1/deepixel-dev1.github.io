#pragma once

#include <opencv2/opencv.hpp>

namespace dp {
    namespace aringnative {

        struct DPARingResult {
            bool detected;
            cv::Point leftEarringPos;
            cv::Point rightEarringPos;
            cv::Mat mask;
            float earringScaleYLeft;
            float earringScaleYRight;
            float rotX;
            float rotY;

            DPARingResult() {};

            virtual ~DPARingResult() {};
        };

        struct IARing {
            IARing() {};

            virtual ~IARing() {};

            virtual void initialize(int nImgWidth, int nImgHeight,
                                    float sensorWidth, float sensorHeight, float focalLength) = 0;

            // processing
            virtual DPARingResult DetectFace(const cv::Mat src) = 0;

            // get results
            virtual cv::Point getEarringPos(bool isLeft) = 0;
        };
    }
}
