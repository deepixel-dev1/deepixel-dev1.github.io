
![](./img/Deepixel_logo.PNG){: width="200"}  

MAKA API TUTORIAL (For Android)
=========================

>The MAKA API is an API module for face tracking, mosaic, and face recognition.  

***

## Features

### 1. MAKA Face Tracking

>MAKA Face Tracking is a system that finds faces at high speed and keeps track of face positions.  
![](./img/FT_SS.png){: width="600"}  

* SPEC:
	* Up to 3 peoples
	* Unconstraind face tracker  
	* 30~40 FPS (VGA:640X480, Android Mobile:Galaxy Note 8)
	* Smoothing face rectangle
 
* OUTPUT:  
	* Face Rectangle(x, y, width, height)  
	![](./img/FacePosition.png){: width="400"}	

### 2. MAKA Face Mosaic

### 3. MAKA Face Recognition

*****

## MAKA API Files
 * Maka API(C++)  
   
   ```
   MAKA/include/opencv_3.3.1/                : OpenCV library include folder
   MAKA/include/IMaka.h                      : Maka API Interface Header file
   MAKA/include/DPException.h                : Maka Exception Header file
   MAKA/include/DPFactoryForAndroid.h        : Maka Singleton Header file
   MAKA/libs/armeabi-v7a/libopencv_world.so  : OpenCV library file
   MAKA/libs/armeabi-v7a/libtbb.so           : tbb library file
   MAKA/libs/armeabi-v7a/libmakanative.so    : Maka library file
   ```     

*****

## HOW TO USE [MAKA API][api] (For Android)
![](./img/Logic.png){: width="200"}   

1. Insert Library files into the library directory that matches the project's target hardware
2. Insert Include files into your project's include directory.
3. Create the IMaka Object at the location you want to use.  
```
std::shared_ptr<dp::makanative::IMaka> g_ptrMaka;
g_ptrMaka = dp::android::DPFactoryForAndroid::CreateInstance<dp::makanative::IMaka>(env,
                                                                                    activity,
                                                                                    licenseFilename);
```  
4. Before the MAKA API is executed, you must call the the initialize function of the IMaka.  
```
g_ptrMaka->initialize(img.cols, img.rows);
```  
5. When the MAKA API is needed, call the process function of IMaka. 
```
g_ptrMaka->process(img);
```

*****

## Reference

- [MAKA API][api]
- [OpenCV][opencv]
- [TBB][tbb]

[opencv]: http://opencv.org/
[api]: https://deepixel-dev1.github.io/makanative/api/
[tbb]: https://www.threadingbuildingblocks.org/

