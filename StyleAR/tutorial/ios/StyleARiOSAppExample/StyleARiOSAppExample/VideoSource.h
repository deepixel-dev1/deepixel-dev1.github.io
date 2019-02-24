//
//  VideoSource.h
//  MinimumOpenCVLiveCamera
//
//  Created by Akira Iwaya on 2015/11/05.
//  Copyright © 2015年 akira108. All rights reserved.
//

#ifdef __cplusplus
#include <opencv2/opencv.hpp>
#endif

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <StyleARiOS/DPStyleAR.h>

#define FACEAR
//#define HANDAR
//#define WRISTAR

@interface VideoSource : NSObject

- (void)start;
- (void)stop;
- (DPFaceMetaData *)getFaceMetaData;

@property id<DPStyleAR> styleAR;
@property (nonatomic, strong) UIView *targetView;
@property (strong, nonatomic) AVCaptureSession *captureSession;

@end

