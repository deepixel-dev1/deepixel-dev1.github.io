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
#import <StyleARRing/StyleARRing.h>

NSString *GetPathFromResource(NSString *filename, NSString *extension) {
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:filename ofType:extension];
    return resourcePath;
}

CGImageRef GetImgRef(NSString *filename, NSString *extension) {
    NSString *resourcePath = GetPathFromResource(filename, extension);
    CGDataProviderRef dataProvider = CGDataProviderCreateWithFilename([resourcePath UTF8String]);
    CGImageRef imgRef = CGImageCreateWithPNGDataProvider(dataProvider, NULL, true, kCGRenderingIntentDefault);
    return imgRef;
}

@interface StyleARWrapper ()
@property(nonatomic, strong)DPStyleARRingView *styleARRingView;
@end

@implementation StyleARWrapper

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super init];
    if (self) {
        _styleARRingView = [[DPStyleARRingView alloc] initWithFrame:frame];
        
        // Ring parameters.
        DPRingParam *ringParam = [[DPRingParam alloc] init];
        ringParam.cgImage = GetImgRef(@"ring_3", @"png");
        ringParam.offset = 0.5f;
        [_styleARRingView setRingParam:ringParam finger:DP_FINGER_RING];

        // Hand type
        [_styleARRingView setHandType:true];
        
        // Hand template.
        [_styleARRingView setHandTemplateEnabled:YES];
        [_styleARRingView setHandTemplate:GetImgRef(@"Hand_guide", @"png")];
    }
    return self;
}

- (UIView *)getStyleARRingView {
    return _styleARRingView;
}

- (void)start {
    [_styleARRingView start];
}

- (void)stop {
    [_styleARRingView stop];
}

- (NSString *)getHandMetaData {
    DPHandMetaData *handMetaData = [_styleARRingView getHandMetaData];
    NSMutableString *str = [[NSMutableString alloc] init];
    [str appendFormat:@"FPR = %f\n", handMetaData.finger2palmRatio];
    [str appendFormat:@"FL = %f\n", [[handMetaData.fingerLengths objectAtIndex:DP_FINGER_RING] floatValue]];
    [str appendFormat:@"FW = %f\n", [[handMetaData.fingerWidths objectAtIndex:DP_FINGER_RING] floatValue]];
    [str appendFormat:@"NCM = #%06X\n", 0xFFFFFF & handMetaData.nailColorMean];
    [str appendFormat:@"NCS = #%06X\n", 0xFFFFFF & handMetaData.nailColorStd];
    [str appendFormat:@"SCM = #%06X\n", 0xFFFFFF & handMetaData.skinColorMean];
    [str appendFormat:@"SCS = #%06X", 0xFFFFFF & handMetaData.skinColorStd];
    return str;
}

@end
