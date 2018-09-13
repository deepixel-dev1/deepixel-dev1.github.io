//
//  FrameProcessor.h
//  MinimumOpenCVLiveCamera
//
//  Created by Akira Iwaya on 2015/11/05.
//  Copyright © 2015年 akira108. All rights reserved.
//

#ifdef __cplusplus
#include <opencv2/opencv.hpp>
#endif

#import <Foundation/Foundation.h>
#import "VideoSource.h"
#import <AVFoundation/AVFoundation.h>

@interface FrameProcessor : NSObject<VideoSourceDelegate>

- (void)processFrame:(cv::Mat &)frame;

@end
