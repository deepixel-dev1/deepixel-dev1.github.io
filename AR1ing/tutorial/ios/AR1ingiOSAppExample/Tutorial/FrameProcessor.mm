//
//  FrameProcessor.m
//  MinimumOpenCVLiveCamera
//
//  Created by Akira Iwaya on 2015/11/05.
//  Copyright © 2015年 akira108. All rights reserved.
//

#import "FrameProcessor.h"
#import "AR1ingiOS/AR1ingiOS.h"

#import "memory.h"

std::shared_ptr<dp::ar1ingnative::IAR1ing> g_styleAR;
cv::Mat g_matEarring;
cv::Mat g_matRotatedEarring;
cv::Point g_earringAnchorPos;
float g_earringScale = 1.0f;
cv::Mat g_matRing;
cv::Mat g_matBand;

std::string GetEarringImgPath() {
    static std::string strResourcePath = "";
    if(strResourcePath != "")
        return strResourcePath;
    
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:@"bvlgari_earing" ofType:@"png"];
    
    strResourcePath = std::string([resourcePath UTF8String]);
    return strResourcePath;
}

std::string GetRingImgPath() {
    static std::string strResourcePath = "";
    if(strResourcePath != "")
        return strResourcePath;
    
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:@"ring_mask" ofType:@"png"];
    
    strResourcePath = std::string([resourcePath UTF8String]);
    return strResourcePath;
}

std::string GetBandImgPath() {
    static std::string strResourcePath = "";
    if(strResourcePath != "")
        return strResourcePath;
    
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:@"band_mask" ofType:@"png"];
    
    strResourcePath = std::string([resourcePath UTF8String]);
    return strResourcePath;
}

void drawEarring(cv::Mat &dst, cv::Mat &mask, bool isLeft, cv::Point earringPos, float rotX, float earringScaleY) {
    if(earringPos.x <= 0 || earringPos.y <= 0 || earringPos.x >= dst.cols || earringPos.y >= dst.rows) {
        return;
    }
    
    cv::Mat matEarring;
    float earringScale = g_earringScale * earringScaleY * 0.7f;
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

cv::Point2d getOrthogornal(cv::Point2d pt1, cv::Point2d pt2) {
    cv::Point2d diffPt = pt2 - pt1;
    
    float l = std::sqrt(diffPt.x * diffPt.x + diffPt.y * diffPt.y);
    cv::Point2f smallDiff = diffPt / l;
    
    return cv::Point2d(smallDiff.y, -smallDiff.x);
}

void draw_band_or_ring(cv::InputOutputArray _matDisplay, std::vector<cv::Point2f> pts,
                       float thickness, cv::Mat matBand) {
    cv::Mat matDisplay = _matDisplay.getMat();
    
    std::vector<cv::Point2f> vWristPt = pts;
    std::vector<cv::Point2f> imagePt;
    
    vWristPt.resize(3);
    imagePt.resize(3);
    
    int nRingThickness = thickness * matBand.cols;
    imagePt[0] = cv::Point2f(nRingThickness, matBand.rows / 2);
    imagePt[1] = cv::Point2f(matBand.cols - nRingThickness, matBand.rows / 2);
    imagePt[2] = cv::Point2f(matBand.cols / 2, 0);
    
    cv::Point2f wristCt = cv::Point2f(vWristPt[0] + vWristPt[1]) / 2.0;
    cv::Point2f diffPt = vWristPt[1] - vWristPt[0];
    float wristWidth = std::sqrt(diffPt.x * diffPt.x + diffPt.y * diffPt.y);
    
    cv::Point2d ortho_vec = getOrthogornal(cv::Point2d(vWristPt[0]), cv::Point2d(vWristPt[1]));
    
    float wristHeight = wristWidth * (matBand.rows - nRingThickness * 2) / matBand.cols;
    vWristPt[2] = wristCt + cv::Point2f(ortho_vec) * wristHeight;
    
    // Find the original bounding rect
    cv::Rect wristRect = cv::boundingRect(vWristPt);
    
    cv::Point cnt = (wristRect.tl() + wristRect.br()) / 2;
    cv::Point rad = cv::Point(wristRect.width, wristRect.height);
    wristRect = cv::Rect(cnt - rad, cnt + rad);
    
    // Skip when band is out of range
    cv::Rect safeRect(0, 0, matDisplay.cols, matDisplay.rows);
    cv::Rect cropedRect = safeRect & wristRect;
    if (cropedRect.area() != wristRect.area()) {
        return;
    }
    
    // Process the bare minimum area of band
    vWristPt[0] -= cv::Point2f(wristRect.tl());
    vWristPt[1] -= cv::Point2f(wristRect.tl());
    vWristPt[2] -= cv::Point2f(wristRect.tl());
    
    cv::Mat matBand_canvas;
    cv::Mat matWrist(vWristPt);
    cv::Mat matImage(imagePt);
    matWrist = matWrist.reshape(1, 3);
    matImage = matImage.reshape(1, 3);
    
    matWrist.convertTo(matWrist, CV_32F);
    matImage.convertTo(matImage, CV_32F);
    
    cv::Mat matAffine = cv::estimateRigidTransform(matImage, matWrist, false);
    if (matAffine.empty()) {
        return;
    }
    cv::warpAffine(matBand, matBand_canvas, matAffine, wristRect.size());
    
    // Extract useful area from the band image
    cv::Mat bandMask;
    cv::cvtColor(matBand_canvas, bandMask, cv::COLOR_BGR2GRAY);
    bandMask = bandMask > 30;
    
    cv::cvtColor(matBand_canvas, matBand_canvas, CV_BGR2RGBA);
    matBand_canvas.copyTo(matDisplay(wristRect), bandMask);
}

void draw_ring(cv::InputOutputArray _matDisplay,
               dp::ar1ingnative::DPAR1ingHandOutput result) {
    if (!result.bDetected) {
        return;
    }
    
    for (int i = 0; i < 5; i++) {
        if (!result.vFinger[i])
            continue;
        
        std::vector<cv::Point2f> fingerPt;
        for (int j = 0; j < result.vFingerPts[i].size(); j++) {
            fingerPt.push_back(result.vFingerPts[i][j]);
        }
        
        draw_band_or_ring(_matDisplay, fingerPt, 0.1f, g_matRing);
    }
}

void draw_band(cv::InputOutputArray _matDisplay,
               dp::ar1ingnative::DPAR1ingWristOutput result) {
    if (!result.bDetected)
        return;
    
    std::vector<cv::Point2f> vWristPt;
    for (int j = 0; j < result.vWristPt.size(); j++) {
        vWristPt.push_back(result.vWristPt[j]);
    }
    
    draw_band_or_ring(_matDisplay, vWristPt, 0.05f, g_matBand);
}

@implementation FrameProcessor

- (instancetype)init
{
    self = [super init];
    if (self) {
        // Create StyleAR instance.
        g_styleAR = dp::ios::DPFactoryForiOS::CreateInstance<dp::ar1ingnative::IAR1ing>();
        g_styleAR->initialize(1920, 1080, dp::ar1ingnative::DP_IMAGE_ROTATION::CW_90);
        
        std::cout << cv::getBuildInformation() << std::endl;
        
        // Read earring image.
        g_matEarring = cv::imread(GetEarringImgPath(), cv::IMREAD_COLOR);
        cv::cvtColor(g_matEarring, g_matEarring, cv::COLOR_BGR2RGBA);
        
        cv::Mat tmp = g_matEarring.t();
        cv::flip(tmp, g_matRotatedEarring, 0);
        g_earringAnchorPos.x = (int)(g_matRotatedEarring.cols / 8.0f);
        g_earringAnchorPos.y = (int)(g_matRotatedEarring.rows / 2.0f);
        g_earringScale = 1920.0f / (float)g_matRotatedEarring.cols;
        
        g_matRing = cv::imread(GetRingImgPath(), cv::IMREAD_COLOR);
        g_matBand = cv::imread(GetBandImgPath(), cv::IMREAD_COLOR);
    }
    return self;
}

#if defined(FACEAR)

- (void)processFrame:(cv::Mat &)frame {
    // Create an image for destination.
    cv::Mat dst;
    cv::cvtColor(frame, dst, cv::COLOR_BGRA2RGBA);
    
    // Detect face.
    dp::ar1ingnative::DPAR1ingFaceInput input;
    input.matSrc = frame;
    input.enumImageType = dp::ar1ingnative::DP_IMAGE_TYPE::BGRA_8888;
    dp::ar1ingnative::DPAR1ingFaceOutput result = g_styleAR->DetectFace(input);
    
    if(result.bDetected) {
        drawEarring(dst, result.matMask, true, result.ptLeftEarring, result.fRotX, result.fEarringScaleYLeft);
        drawEarring(dst, result.matMask, false, result.ptRightEarring, result.fRotX, result.fEarringScaleYRight);
    }
    
    // Flip
    cv::flip(dst, dst, 0);
    
    cv::cvtColor(dst, frame, cv::COLOR_RGBA2BGRA);
}

#elif defined(HANDAR)



- (void)processFrame:(cv::Mat &)frame {
    // Create an image for destination.
    cv::Mat dst;
    cv::cvtColor(frame, dst, cv::COLOR_BGRA2RGBA);
    
    // Detect hand.
    dp::ar1ingnative::DPAR1ingHandInput input;
    input.matSrc = frame;
    input.enumImageType = dp::ar1ingnative::DP_IMAGE_TYPE::BGRA_8888;
    dp::ar1ingnative::DPAR1ingHandOutput result = g_styleAR->DetectHand(input);
    draw_ring(dst, result);
    
    cv::cvtColor(dst, frame, cv::COLOR_RGBA2BGRA);
}

#else

- (void)processFrame:(cv::Mat &)frame {
    // Create an image for destination.
    cv::Mat dst;
    cv::cvtColor(frame, dst, cv::COLOR_BGRA2RGBA);
    
    // Detect wrist.
    dp::ar1ingnative::DPAR1ingWristInput input;
    input.matSrc = frame;
    input.enumImageType = dp::ar1ingnative::DP_IMAGE_TYPE::BGRA_8888;
    dp::ar1ingnative::DPAR1ingWristOutput result = g_styleAR->DetectWrist(input);
    draw_band(dst, result);
    
    cv::cvtColor(dst, frame, cv::COLOR_RGBA2BGRA);
}

#endif

@end
