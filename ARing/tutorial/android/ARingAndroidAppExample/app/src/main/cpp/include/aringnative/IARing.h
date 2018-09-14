#pragma once

#include <opencv2/opencv.hpp>

namespace dp {
    namespace aringnative {

        /**
         *@brief Result of face detection
         *@details This class contains information about the results of face detection.
         */
        struct DPARingResult {
            bool detected; // Whether or not the face is detected.
            cv::Point leftEarringPos; // Position of left earring.
            cv::Point rightEarringPos; // Position of right earring.
            cv::Mat mask; // Mask representing the face area. By using this, a part of the earring can be seen.
            float earringScaleYLeft; // Earring size scale. this value is screen height and left ear height ratio.
            float earringScaleYRight; // Earring size scale. this value is screen height and right ear height ratio.
            float rotX; // Yaw
            float rotY; // Pitch

            DPARingResult() {};

            virtual ~DPARingResult() {};
        };

        /**
         *@brief API control interface
         *@details This class is interface face detection.
         */
        struct IARing {
            IARing() {};

            virtual ~IARing() {};

            /**
             *@brief
             *@details Initialize this module. This method should be called before using this module.
             *@param[in] width of input image
             *@param[in] height of input image
             *@param[in] width of image sensor of device
             *@param[in] heght of image sensor of device
             *@param[in] focal length of camera
             */
            virtual void initialize(int nImgWidth, int nImgHeight,
                                    float sensorWidth, float sensorHeight, float focalLength) = 0;

            /**
             *@brief
             *@details Detect face and return information related to ear position and scale.
             *@param[in] input image
             *@return information related to ear position and scale
             */
            virtual DPARingResult DetectFace(const cv::Mat src) = 0;
        };
    }
}
