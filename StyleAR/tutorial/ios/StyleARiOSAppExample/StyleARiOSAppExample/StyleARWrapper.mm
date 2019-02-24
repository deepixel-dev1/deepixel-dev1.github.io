//
//  StyleARWrapper.mm
//  TestStyleARiOSApp
//
//  Created by dpxl on 29/08/2018.
//  Copyright © 2018 deepixel. All rights reserved.
//

#ifdef __cplusplus
#include <opencv2/opencv.hpp>
#endif

#import "StyleARWrapper.h"
#import "VideoSource.h"
#import <StyleARiOS/DPStyleAR.h>
#import <StyleARiOS/DPStyleARFactory.h>

@interface StyleARWrapper ()
@property(nonatomic, strong)VideoSource *videoSource;
@end

@implementation StyleARWrapper

- (instancetype)init
{
    self = [super init];
    if (self) {
        _videoSource = [[VideoSource alloc] init];
    }
    return self;
}

- (void)setTargetView:(UIView *)view {
    _videoSource.targetView = view;
}

- (void)start {
    [self.videoSource start];
}

- (NSString *)getMetaData {
    DPFaceMetaData *faceMetaData = [self.videoSource getFaceMetaData];
    NSMutableString *str = [[NSMutableString alloc] init];
    [str appendFormat:@"FRM = %f\n", faceMetaData.faceRatioMean];
    [str appendFormat:@"FRS = %f\n", faceMetaData.faceRatioStd];
    [str appendFormat:@"HCM = #%06X\n", 0xFFFFFF & faceMetaData.hairColorMean];
    [str appendFormat:@"HCS = #%06X\n", 0xFFFFFF & faceMetaData.hairColorStd];
    [str appendFormat:@"LCM = #%06X\n", 0xFFFFFF & faceMetaData.lipColorMean];
    [str appendFormat:@"LCS = #%06X\n", 0xFFFFFF & faceMetaData.lipColorStd];
    [str appendFormat:@"SCM = #%06X\n", 0xFFFFFF & faceMetaData.skinColorMean];
    [str appendFormat:@"SCS = #%06X", 0xFFFFFF & faceMetaData.skinColorStd];
    return str;
}

@end
