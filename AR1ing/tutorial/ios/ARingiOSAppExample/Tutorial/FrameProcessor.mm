//
//  FrameProcessor.m
//  MinimumOpenCVLiveCamera
//
//  Created by Akira Iwaya on 2015/11/05.
//  Copyright © 2015年 akira108. All rights reserved.
//

#import "FrameProcessor.h"
#import "ARingiOS/ARingiOS.h"

#import "memory.h"

std::shared_ptr<dp::aringnative::IARing> g_styleAR;
cv::Mat g_matEarring;
cv::Mat g_matRotatedEarring;
cv::Point g_earringAnchorPos;
float g_earringScale = 1.0f;

std::string GetEarringImgPath() {
    static std::string strResourcePath = "";
    if(strResourcePath != "")
        return strResourcePath;
    
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:@"bvlgari_earing" ofType:@"png"];
    
    strResourcePath = std::string([resourcePath UTF8String]);
    return strResourcePath;
}

void drawEarring(cv::Mat &dst, cv::Mat &mask, bool isLeft, cv::Point earringPos, float rotX, float earringScaleY) {
    if(earringPos.x <= 0 || earringPos.y <= 0 || earringPos.x >= dst.cols || earringPos.y >= dst.rows) {
        return;
    }
    
    cv::Mat matEarring;
    float earringScale = g_earringScale * earringScaleY * 0.6f;
    cv::resize(g_matRotatedEarring, matEarring, cv::Size(0,0), earringScale, earringScale);
    
    cv::Rect earringRect(0, 0, matEarring.cols, matEarring.rows);
    earringRect = earringRect + earringPos - (g_earringAnchorPos * earringScale);

    cv::Rect safeRect(0, 0, dst.cols, dst.rows);
    earringRect = earringRect & safeRect;
    
    if(matEarring.size().area() != earringRect.area())
        return;
    
    // To apply mas only when its not visible.
    if(isLeft && rotX >= -15) {
        matEarring.setTo(0, mask(earringRect));
    } else if(!isLeft && rotX <= 15) {
        matEarring.setTo(0, mask(earringRect));
    }
    
    cv::Mat earringMask;
    cv::compare(matEarring, 0, earringMask, cv::CMP_GT);
    
    cv::subtract(dst(earringRect), earringMask, dst(earringRect));
    cv::add(matEarring, dst(earringRect), dst(earringRect));
}

@implementation FrameProcessor

- (instancetype)init
{
    self = [super init];
    if (self) {
        // Create StyleAR instance.
        g_styleAR = dp::ios::DPFactoryForiOS::CreateInstance<dp::aringnative::IARing>();
        g_styleAR->initialize(1920, 1080, dp::aringnative::DP_IMAGE_ROTATION::CW_90);

        // Read earring image.
        g_matEarring = cv::imread(GetEarringImgPath(), cv::IMREAD_COLOR);
        cv::cvtColor(g_matEarring, g_matEarring, cv::COLOR_BGR2RGBA);
        
        cv::Mat tmp = g_matEarring.t();
        cv::flip(tmp, g_matRotatedEarring, 0);
        g_earringAnchorPos.x = (int)(g_matRotatedEarring.cols / 8.0f);
        g_earringAnchorPos.y = (int)(g_matRotatedEarring.rows / 2.0f);
        g_earringScale = 1920.0f / (float)g_matRotatedEarring.cols;
    }
    return self;
}

- (void)processFrame:(cv::Mat &)frame {
    // Create RGBA image for destination.
    cv::Mat dst;
    cv::cvtColor(frame, dst, cv::COLOR_BGRA2RGBA);

    // Detect face.
    dp::aringnative::DPARingResult result = g_styleAR->DetectFace(frame, dp::aringnative::DP_IMAGE_TYPE::BGRA_8888);
    
    if(result.detected) {
        drawEarring(dst, result.mask, true, result.leftEarringPos, result.rotX, result.earringScaleYLeft);
        drawEarring(dst, result.mask, false, result.rightEarringPos, result.rotX, result.earringScaleYRight);
    }

    // Flip
    cv::flip(dst, dst, 0);
    
    // Copy dst to frame.
    cv::cvtColor(dst, frame, cv::COLOR_RGBA2BGRA);
}

@end
