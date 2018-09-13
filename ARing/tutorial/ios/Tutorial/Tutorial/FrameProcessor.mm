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
    cv::resize(g_matEarring, matEarring, cv::Size(0,0), earringScaleY, earringScaleY);
    
    cv::Rect earringRect(0, 0, matEarring.cols, matEarring.rows);
    earringRect = earringRect + earringPos;
    earringRect = earringRect - cv::Point(earringRect.width / 2, earringRect.width / 4);
    
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
        g_styleAR->initialize(480, 640, 2.4f, 3.2f, 2.95f);

        std::cout << cv::getBuildInformation() << std::endl;
        
        // Read earring image.
        g_matEarring = cv::imread(GetEarringImgPath(), cv::IMREAD_COLOR);
        cv::cvtColor(g_matEarring, g_matEarring, cv::COLOR_BGR2RGBA);
    }
    return self;
}

- (void)processFrame:(cv::Mat &)frame {
    // Flip and transpose
    cv::Mat src;
    cv::transpose(frame, src);
    cv::flip(src, src, 1);

    // Create gray image.
    cv::Mat srcGray;
    cv::cvtColor(src, srcGray, cv::COLOR_BGRA2GRAY);

    // Create RGBA image for destination.
    cv::Mat dst;
    cv::cvtColor(src, dst, cv::COLOR_BGRA2RGBA);

    // Detect face.
    dp::aringnative::DPARingResult result = g_styleAR->DetectFace(srcGray);
    
    if(result.detected) {
        drawEarring(dst, result.mask, true, result.leftEarringPos, result.rotX, result.earringScaleYLeft);
        drawEarring(dst, result.mask, false, result.rightEarringPos, result.rotX, result.earringScaleYRight);
    }

    // Transpose.
    cv::Mat _frame;
    cv::transpose(dst, _frame);

    // Copy dst to frame.
    cv::cvtColor(_frame, frame, cv::COLOR_RGBA2BGRA);
}

@end
