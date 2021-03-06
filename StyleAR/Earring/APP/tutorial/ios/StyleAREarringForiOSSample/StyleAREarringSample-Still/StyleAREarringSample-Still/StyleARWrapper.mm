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
@property(nonatomic, strong)DPStyleAREarring *styleAREarring;
@end

@implementation StyleARWrapper

- (instancetype)init {
    self = [super init];
    if (self) {
        _styleAREarring = [DPStyleAREarring sharedInstance];

        // Ring parameters.
        DPEarringParam *earringParam = [[DPEarringParam alloc] init];
        earringParam.cgImage = GetImgRef(@"earring", @".png");
        earringParam.size = CGSizeMake(20.0f , 30.0f);
        earringParam.anchorPoint = CGPointMake(10.0f, 1.0f);
        [_styleAREarring setEarringParam:earringParam];
    }
    return self;
}

- (void)start {
    [_styleAREarring start];
}

- (void)stop {
    [_styleAREarring stop];
}

- (NSString *)getFaceMetaData {
    DPFaceMetaData *faceMetaData = [_styleAREarring getFaceMetaData];
    NSMutableString *str = [[NSMutableString alloc] init];
    [str appendFormat:@"FRM = %f\n", faceMetaData.faceRatioMean];
    [str appendFormat:@"CRM = %f\n", faceMetaData.chinLineRatioMean];
    [str appendFormat:@"HCM = #%06X\n", 0xFFFFFF & faceMetaData.hairColorMean];
    [str appendFormat:@"HCS = #%06X\n", 0xFFFFFF & faceMetaData.hairColorStd];
    [str appendFormat:@"LCM = #%06X\n", 0xFFFFFF & faceMetaData.lipColorMean];
    [str appendFormat:@"LCS = #%06X\n", 0xFFFFFF & faceMetaData.lipColorStd];
    [str appendFormat:@"SCM = #%06X\n", 0xFFFFFF & faceMetaData.skinColorMean];
    [str appendFormat:@"SCS = #%06X\n", 0xFFFFFF & faceMetaData.skinColorStd];
    [str appendFormat:@"DCM = #%06X\n", 0xFFFFFF & faceMetaData.dressColorMean];
    [str appendFormat:@"DCS = #%06X", 0xFFFFFF & faceMetaData.dressColorStd];
    return str;
}

- (CGImageRef)getStyleARImage {
    CGImageRef cgImage = GetImgRef(@"face", @".jpg");
    return [_styleAREarring getStyleARImage:cgImage];
}

- (void)setFilterEnabled:(bool)enabled {
    [_styleAREarring setFilterEnabled:enabled];
}

- (void)setEarringFilterEnabled:(bool)enabled {
    [_styleAREarring setEarringFilterEnabled:enabled];
}

@end
