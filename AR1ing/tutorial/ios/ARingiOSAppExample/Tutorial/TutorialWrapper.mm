//
//  TutorialWrapper.m
//  Tutorial
//
//  Created by dpxl on 13/09/2018.
//  Copyright © 2018 deepixel. All rights reserved.
//

#ifdef __cplusplus
#include <opencv2/opencv.hpp>
#endif

#import "TutorialWrapper.h"
#import "FrameProcessor.h"
#import "VideoSource.h"

@interface TutorialWrapper ()
@property(nonatomic, strong)FrameProcessor *frameProcessor;
@property(nonatomic, strong)VideoSource *videoSource;
@end

@implementation TutorialWrapper

- (instancetype)init
{
    self = [super init];
    if (self) {
        _frameProcessor = [[FrameProcessor alloc] init];
        _videoSource = [[VideoSource alloc] init];
        _videoSource.delegate = _frameProcessor;
    }
    return self;
}

- (void)setTargetView:(UIView *)view {
    self.videoSource.targetView = view;
}

- (void)start {
    [self.videoSource start];
}

@end
