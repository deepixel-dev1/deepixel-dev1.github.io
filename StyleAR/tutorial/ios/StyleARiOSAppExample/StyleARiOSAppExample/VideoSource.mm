//
//  VideoSource.m
//  MinimumOpenCVLiveCamera
//
//  Created by Akira Iwaya on 2015/11/05.
//  Copyright © 2015年 akira108. All rights reserved.
//

#import "VideoSource.h"
#import <AVFoundation/AVFoundation.h>
#import <Accelerate/Accelerate.h>
#import <StyleARiOS/StyleARiOS.h>

using namespace cv;
using namespace std;

/**
 * 귀걸이의 절대 경로를 반환한다.
 */
NSString *GetEarringImgPath() {
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:@"wing1" ofType:@"png"];
    return resourcePath;
}

@implementation VideoSource

@synthesize captureSession;

/**
 * StyleAR 결과를 출력할 view를 설정한다.
 */
- (void)setTargetView:(UIView *)targetView {
    // 센서 방향대로 영상을 회전한다.
    CGAffineTransform rotation = CGAffineTransformMakeRotation(M_PI / 2);
    // 전면 카메라가 거울처럼 보이도록 좌우를 뒤집는다.
    CGAffineTransform mirror = CGAffineTransformMakeScale(-1.0, 1.0);

    targetView.layer.affineTransform = CGAffineTransformConcat(rotation, mirror);
    targetView.layer.contentsGravity = kCAGravityResizeAspect;
    targetView.layer.frame = targetView.bounds;

    // StyleAR에 view를 설정한다.
    [_styleAR setTargetView:targetView];
}

/**
 * 전면 카메라
 */
- (AVCaptureDevice *)frontCamera {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in devices) {
        if ([device position] == AVCaptureDevicePositionFront) {
            return device;
        }
    }
    return nil;
}

/**
 * 후면 카메라
 */
- (AVCaptureDevice *)backCamera {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in devices) {
        if ([device position] == AVCaptureDevicePositionBack) {
            return device;
        }
    }
    return nil;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        // 캡쳐 세션을 생성하고 초기화한다.
        captureSession = [[AVCaptureSession alloc] init];
        captureSession.sessionPreset = AVCaptureSessionPresetPhoto;

        // 캡쳐 세션의 input을 설정한다.
        AVCaptureDevice *device = [self frontCamera];
        NSError *error = nil;
        AVCaptureDeviceInput *input = [[AVCaptureDeviceInput alloc] initWithDevice:device error:&error];
        [captureSession addInput:input];

        // 갭쳐 세션의 output을 설정한다.
        AVCaptureVideoDataOutput *output = [[AVCaptureVideoDataOutput alloc] init];
        output.videoSettings = @{(NSString *)kCVPixelBufferPixelFormatTypeKey : @(kCVPixelFormatType_32BGRA)};
        output.alwaysDiscardsLateVideoFrames = YES;
        [captureSession addOutput:output];
        
        // StyleAR 객체를 생성하고 초기화한다.
        _styleAR = [DPStyleARFactory getInstance];
        @try {
            [_styleAR initialize];
        }
        @catch(DPLicenseExpiredException *e) {
            std::cout << [e reason] << std::endl;
        }
        @catch(DPLicenseException *e) {
            std::cout << [e reason] << std::endl;
        }
        @catch(DPException *e) {
            std::cout << [e reason] << std::endl;
        }

        // 카메라 정보를 설정한다.
        DPCameraParam *cameraParam = [[DPCameraParam alloc] init];
        [cameraParam setSensorOrientation:90];
        [cameraParam setFocalLength:30.0f];
        [_styleAR setCameraParam:cameraParam];
        
        // 귀걸이 정보를 설정한다.
        NSString *earringPath = GetEarringImgPath();
        DPEarringParam *earringParam = [[DPEarringParam alloc] init];
        earringParam.absolutePath = earringPath;
        earringParam.width = 13.0f;
        earringParam.height = 85.0f;
        earringParam.anchorPosition = TOP;
        [_styleAR setEarringParam:earringParam];

        // 카메라 영상을 처리하는 대리자를 지정한다.
        id<AVCaptureVideoDataOutputSampleBufferDelegate> dele = [_styleAR getDelegate];
        dispatch_queue_t queue = dispatch_queue_create("VideoQueue", DISPATCH_QUEUE_SERIAL);
        [output setSampleBufferDelegate:dele queue:queue];
    }
    
    return self;
}

/**
 * 카메라와 StyleAR를 시작한다.
 */
- (void)start {
    [self.captureSession startRunning];
    [self.styleAR start];
}

/**
 * 카메라와 StyleAR를 정지한다.
 */
- (void)stop {
    [self.captureSession stopRunning];
    [self.styleAR stop];
}

/**
 * 얼굴의 메타 정보를 반환한다.
 */
- (DPFaceMetaData *)getFaceMetaData {
    return [self.styleAR getFaceMetaData];
}
@end
