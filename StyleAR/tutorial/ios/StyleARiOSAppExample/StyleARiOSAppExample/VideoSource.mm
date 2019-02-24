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
#import <StyleARiOS/DPStyleARFactory.h>

using namespace cv;
using namespace std;

NSString *GetEarringImgPath() {
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:@"wing1" ofType:@"png"];
    return resourcePath;
}

@implementation VideoSource

@synthesize captureSession;

- (void)setTargetView:(UIView *)targetView {
    CGAffineTransform rotation = CGAffineTransformMakeRotation(M_PI / 2);
    CGAffineTransform mirror = CGAffineTransformMakeScale(-1.0, 1.0);
    targetView.layer.affineTransform = CGAffineTransformConcat(rotation, mirror);
    targetView.layer.contentsGravity = kCAGravityResizeAspect;
    targetView.layer.frame = targetView.bounds;

    [_styleAR setTargetView:targetView];
}

- (AVCaptureDevice *)frontCamera {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in devices) {
        if ([device position] == AVCaptureDevicePositionFront) {
            return device;
        }
    }
    return nil;
}

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
        captureSession = [[AVCaptureSession alloc] init];
        captureSession.sessionPreset = AVCaptureSessionPresetPhoto;

#if defined(FACEAR)
        AVCaptureDevice *device = [self frontCamera];
#else
        AVCaptureDevice *device = [self backCamera];
#endif
        
        NSError *error = nil;
        AVCaptureDeviceInput *input = [[AVCaptureDeviceInput alloc] initWithDevice:device error:&error];
        [captureSession addInput:input];
        
        AVCaptureVideoDataOutput *output = [[AVCaptureVideoDataOutput alloc] init];
        output.videoSettings = @{(NSString *)kCVPixelBufferPixelFormatTypeKey : @(kCVPixelFormatType_32BGRA)};
        output.alwaysDiscardsLateVideoFrames = YES;
        [captureSession addOutput:output];
        
        _styleAR = [DPStyleARFactory getInstance];
        DPCameraParam *cameraParam = [[DPCameraParam alloc] init];
        [cameraParam setSensorOrientation:90];
        [cameraParam setFocalLength:30.0f];
        [_styleAR initialize];
        [_styleAR setCameraParam:cameraParam];
        NSString *earringPath = GetEarringImgPath();
        DPEarringParam *earringParam = [[DPEarringParam alloc] init];
        earringParam.absolutePath = earringPath;
        earringParam.width = 13.0f;
        earringParam.height = 85.0f;
        earringParam.anchorPosition = TOP;
        [_styleAR setEarringParam:earringParam];

        id<AVCaptureVideoDataOutputSampleBufferDelegate> dele = [_styleAR getDelegate];
        dispatch_queue_t queue = dispatch_queue_create("VideoQueue", DISPATCH_QUEUE_SERIAL);
        [output setSampleBufferDelegate:dele queue:queue];
    }
    
    return self;
}

- (void)start {
    [self.captureSession startRunning];
    [self.styleAR start];
}

- (void)stop {
    [self.captureSession stopRunning];
    [self.styleAR stop];
}

- (DPFaceMetaData *)getFaceMetaData {
    return [self.styleAR getFaceMetaData];
}
@end
