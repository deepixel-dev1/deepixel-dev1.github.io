//
//  StyleARWrapper.mm
//  TestStyleARiOSApp
//
//  Created by dpxl on 29/08/2018.
//  Copyright © 2018 deepixel. All rights reserved.
//

//#ifdef __cplusplus
//#include <opencv2/opencv.hpp>
//#endif

#import "StyleARWrapper.h"
#import <StyleAREarring/StyleAREarring.h>

NSString *GetPathFromResource(NSString *filename, NSString *extension) {
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *resourcePath = [bundle pathForResource:filename ofType:extension];
    return resourcePath;
}

CGImageRef GetImgRef(NSString *filename, NSString *extension) {
    NSString *resourcePath = GetPathFromResource(filename, extension);
    CGDataProviderRef dataProvider = CGDataProviderCreateWithFilename([resourcePath UTF8String]);
    if([extension isEqual: @".png"]) {
        CGImageRef imgRef = CGImageCreateWithPNGDataProvider(dataProvider, NULL, true, kCGRenderingIntentDefault);
        return imgRef;
    } else if([extension isEqual: @".jpg"]) {
        CGImageRef imgRef = CGImageCreateWithJPEGDataProvider(dataProvider, NULL, true, kCGRenderingIntentDefault);
        return imgRef;
    }
    return nil;
}

@interface StyleARWrapper ()
@property(nonatomic, strong)DPStyleAREarringView *styleAREarringView;
@property(nonatomic, strong)DPStyleAREarring *styleAREarring;
@end

@implementation StyleARWrapper

- (instancetype)initWithFrame:(UIView *)frame {
    self = [super init];
    if (self) {
        _styleAREarring = [DPStyleAREarring sharedInstance];

        _styleAREarringView = [[DPStyleAREarringView alloc] initWithFrame:frame.bounds];

        [frame addSubview:_styleAREarringView];
        [frame sendSubviewToBack:_styleAREarringView];

        // Ring parameters.
        DPEarringParam *earringParam = [[DPEarringParam alloc] init];
        earringParam.cgImage = GetImgRef(@"earring", @".png");
        earringParam.size = CGSizeMake(20.0f , 30.0f);
        earringParam.anchorPoint = CGPointMake(10.0f, 1.0f);
        [_styleAREarringView setEarringParam:earringParam];
    }
    return self;
}

- (UIView *)getStyleAREarringView {
    return _styleAREarringView;
}

- (void)start {
    [_styleAREarringView start];
}

- (void)stop {
    [_styleAREarringView stop];
}

- (NSString *)getFaceMetaData {
    DPFaceMetaData *faceMetaData = [_styleAREarringView getFaceMetaData];
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

- (CGImageRef)getStyleARImage {
    CGImageRef cgImage = GetImgRef(@"face", @".jpg");
    return [_styleAREarring getStyleARImage:cgImage];
}

- (CGImageRef)getCurrentStyleARImage {
    return [_styleAREarringView getStyleARImage];
}

- (CGImageRef)getCameraImage {
    return [_styleAREarringView getCameraImage];
}

- (void)setFilterEnabled:(bool)enabled {
    [_styleAREarringView setFilterEnabled:enabled];
}

- (void)setEarringFilterEnabled:(bool)enabled {
    [_styleAREarringView setEarringFilterEnabled:enabled];
}

@end
