//
//  StyleARWrapper.h
//  TestStyleARiOSApp
//
//  Created by dpxl on 29/08/2018.
//  Copyright © 2018 deepixel. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreImage/CoreImage.h>
#import <AVFoundation/AVFoundation.h>
#import <CoreGraphics/CGImage.h>
#import <CoreGraphics/CoreGraphics.h>

@interface StyleARWrapper : NSObject

- (instancetype)init;
- (void)start;
- (void)stop;
- (CGImageRef)getStyleARImage;
- (void)setFilterEnabled:(bool)enabled;
- (void)setEarringFilterEnabled:(bool)enabled;

@end
