<img src="img/Deepixel_logo.PNG" width=200></img> 

MAKA API TUTORIAL (For Android)
=========================

>The MAKA API is an API module for face tracking, mosaic, and face recognition.  



***
## Features

### 1. MAKA Face Tracking

>MAKA Face Tracking is a system that finds faces at high speed and keeps track of face positions. 
<img src="img/FT_SS.png" width=600></img>

* SPEC:
 * Max 3 peoples
 * Unconstraind face tracker  
 * 30~40 FPS (VGA, Android Mobile:Galaxy Note 8)
 * Smoothing face rectangle
 
* OUTPUT:
 * Face Position(x, y, width, height)   
<img src="img/FacePosition.png" width=400></img>

### 2. MAKA Face Mosaic

### 3. MAKA Face Recognition

*****

## MAKA API Files
 * Maka API(C++)  
   
   ```
         MAKA/include/opencv_3.1                  : OpenCV library include files
         MAKA/include/tbb_4.3                     : TBB library include files
         MAKA/include/DPMakaController.h          : Maka API Controller file
         MAKA/include/DPMakaMessage.h			  : Maka API Message file
         MAKA/libs/armeabi-v7a/libopencv_java3.so : OpenCV library file
         MAKA/libs/armeabi-v7a/libMaka.so         : Maka library file 
   ```
   
 * Maka Sample Project With Android
  
  ```
         MAKA/Android/Sample/MakaTest             : Maka Android Sample Project files
  ```

*****

## HOW TO USE [MAKA API][api] (For Android)
<img src="img/Logic.png" width=200></img>  

1. Insert Library files into the library directory that matches the project's target hardware
2. Insert Include files into your project's include directory.
3. Include DPMakerController.h in the class header to use the MAKA API, and create a DPMakerController Class member variable.
4. Before the MAKA API is executed, the initialize function of the DPMakerController is called  
	ex) ```DPMakaController.initialize();```
5. When the MAKA API is needed, call the process function of DPMakaController.  
	ex) ```DPMakaController.process(IMAGE, FACE_TRACKING);```
6. When the MAKA API needs to be shut down, it calls the process function of the DPMakerController.   
	ex) ```DPMakaController.process(cv::Mat(), FACE_TRACKING_RELEASE);```

*****

## MAKA API Return Messages

```
    _MAKA_MSG_DONE_               : The function completed successfully.
    _MAKA_MSG_NO_INITIALIZATION_  : Not initialized.
    _MAKA_MSG_NO_IMAGE_           : There is no input image.
    _MAKA_MSG_NO_SAME_IMAGE_SIZE_ : The initial image size and the input image size are different.
    _MAKA_MSG_INITIALIZED_        : Initialization has already been executed.
```

*****

## MAKA API MODE

```
    FACE_TRACKING                 : Face Tracking Mode
    FACE_TRACKING_RELEASE         : Face Tracking Release Mode
```

***

## Reference

- [MAKA API][api]
- [OpenCV][opencv]
- [TBB][tbb]

[opencv]: http://opencv.org/
[api]: http://opencv.org/
[tbb]: https://www.threadingbuildingblocks.org/

