//
//  StyleARWrapper.h
//  TestStyleARiOSApp
//
//  Created by dpxl on 29/08/2018.
//  Copyright © 2018 deepixel. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface StyleARWrapper : NSObject

- (instancetype)initWithFrame:(CGRect)frame;
- (UIView *)getStyleARRingView;
- (void)start;
- (void)stop;
- (NSString *)getHandMetaData;

@end
