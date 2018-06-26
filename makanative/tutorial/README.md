
![](./img/Deepixel_logo.PNG){: width="200"}  

MAKA API TUTORIAL (For Android)
=========================

>The MAKA API is an API module for face tracking, mosaic, and face recognition.  

***

## Features

### 1. MAKA Face Tracking

>MAKA Face Tracking is an API module that detects faces at high speed and keeps the track of face positions.  
![](./img/FT_SS.png){: width="600"}  

* SPEC:
	* Up to 3 people
	* Unconstrained face tracker  
	* 40~70 FPS (Device: Galaxy Note 8)
	* Smoothing the positions of the faces being tracked
 
* OUTPUT:  
	* Face Rectangle(x, y, w, h)  	
	
	```
	x: x-coordinate of the upper-left corner of Face Rectangle
	y: y-coordinate of the upper-left corner of Face Rectangle
	w: width of Face Rectangle
	h: height of Face Rectangle
    ```
         
![](./img/FacePosition.png){: width="400"}	

### 2. MAKA Face Mosaic
>MAKA Face Mosaic is a module that creates a mosaic in the region of a designated face.

* OUTPUT:
	* Image with mosaic  
![](./img/mosaic.PNG){: width="400"}

### 3. MAKA Face Tagging
>MAKA Face Tagging can mark the label for each face and continuously track the labeled face.

* SPEC:
	* The number of labeled faces is up to 3
	* Continuously tracking the labeled faces with occlusion

* Output:
	* Face Rectangle(n)  	
	
	```
	n: label number
    ```
    
![](./img/tagging.gif){: width="400"}

*****
## MAKA API Files
 * Maka API(C++)  
   
   ```
   MAKA/include/opencv_3.3.1/                : OpenCV library folder
   MAKA/include/IMaka.h                      : Maka API Interface Header file
   MAKA/include/DPException.h                : Maka Exception Header file
   MAKA/include/DPFactoryForAndroid.h        : Maka Singleton Header file
   MAKA/libs/armeabi-v7a/libopencv_world.so  : OpenCV library file
   MAKA/libs/armeabi-v7a/libtbb.so           : tbb library file
   MAKA/libs/armeabi-v7a/libmakanative.so    : Maka library file
   ```     

*****

## HOW TO USE [MAKA API][maka_api] (For Android)
![](./img/Logic.png){: width="300"}   

##### 1. Insert "Library files" into the library directory that matches the project's target hardware

##### 2. Insert "Include files" into your project's include directory.

##### 3. Create the IMaka Object at the location you want to use.

  ```
  std::shared_ptr<dp::makanative::IMaka> g_ptrMaka;
  g_ptrMaka = dp::android::DPFactoryForAndroid::CreateInstance<dp::makanative::IMaka>(env,
                                                                                    activity,
                                                                                    licenseFilename);
  ```
##### 4. Before the MAKA API is executed, you must call the initialize function of the IMaka.

  ```
  // (img.cols) is size of image width
  // (img.rows) is size of image height
  g_ptrMaka->initialize(img.cols, img.rows); 
  ```
##### 5. To run the MAKA API, call the process function of IMaka.
  ```
  // All generation of MAKA API are excuted by process function in IMaka
  // Parameters(img and _MAKA_DATA_) without MAKA_MODE has both input and output properties
  g_ptrMaka->process(img, MAKA_MODE, _MAKA_DATA_);
  ```
###### 5.1 Example of Face Tracking API: 
  ```
  // (img) input source image
  // (MAKA_MODE) have to set FACE_TRACKING
  // (_MAKA_DATA_) output the MAKA infomation
  g_ptrMaka->process(img, MAKA_MODE::FACE_TRACKING, _MAKA_DATA_); 
  ```
###### 5.2 Example of Face Tagging API:
  ```
  // (img) unused
  // (MAKA_MODE) have to set FACE_SETTING_LABEL
  // (_MAKA_DATA_) input the position of face that user wants to label
  g_ptrMaka->process(img, MAKA_MODE::FACE_SETTING_LABEL, _MAKA_DATA_); 
  ```
###### 5.3 Example of Face Mosaic API:  
  ```
  // (img) input source image and output image with mosaic
  // (MAKA_MODE) have to set FACE_MOSAIC
  // (_MAKA_DATA_) input the region of a designated face.
  g_ptrMaka->process(img, MAKA_MODE::FACE_MOSAIC, _MAKA_DATA_); 
  ```
*****

## Reference
- [MAKA API][maka_api]
- [OpenCV][opencv]
- [TBB][tbb]

[opencv]: http://opencv.org/
[maka_api]: https://deepixel-dev1.github.io/makanative/api/
[tbb]: https://www.threadingbuildingblocks.org/
[test]: https://youtu.be/_sUGfiMbUE4
[test result]: https://youtu.be/ewjAe0aKql4
