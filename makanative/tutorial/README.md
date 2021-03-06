
![](./img/Deepixel_logo.PNG){: width="200"}  

MAKA API TUTORIAL (For Android)
=========================

>The MAKA API is an API module for face tracking, mosaic, and face recognition.  

***

## Features

### 1. MAKA Face Tracking

>MAKA Face Tracking is an API module that detects faces at high speed and keeps the track of face positions. 
 
![](./img/FT_SS.png){: width="400"}  

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

### 3. MAKA Face Tagging (Labelling)
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

## HOW TO START [MAKA API][maka_api] (For Android)
![](./img/Logic.png){: width="300"}   

#### 1. Insert "Library files" into the library directory that matches the project's target hardware.

#### 2. Insert "Include files" into your project's include directory.

#### 3. Create the IMaka Object at the location you want to use.

  ```
  std::shared_ptr<dp::makanative::IMaka> g_ptrMaka;
  g_ptrMaka = dp::android::DPFactoryForAndroid::CreateInstance<dp::makanative::IMaka>(env,
                                                                                    activity,
                                                                                    licenseFilename);
  ```
#### 4. Before the MAKA API is executed, you must call the initialize function of IMaka.

  ```
  // (img.cols) is size of image width.
  // (img.rows) is size of image height.
  g_ptrMaka->initialize(img.cols, img.rows); 
  ```
#### 5. To run the MAKA API, call the process function of IMaka.

  ```
  // All generation of MAKA API are excuted by process function in IMaka.
  // Parameters(img and _MAKA_DATA_) except MAKA_MODE has both input and output properties.
  g_ptrMaka->process(img, MAKA_MODE, _MAKA_DATA_);
  ```
  
*****  
## Example: HOW TO USE MAKA API (c++ with OpenCV)
>This guide will demonstrate how to use MAKA API(with face tracking, mosaic, and tagging) based on the following scenario. The samples are written in c++ using the OpenCV.

* Scenario steps
Step1. Track the face  
Step2. Set to the face mosaic  
Step3. Designate the face for labelling  
Step4. Apply to the MAKA API with mosaic and face tagging  
Step5. Release the face mosaic  
Step6. Release the face tagging  
Step7. Release the face tracking  
    
* __Step1__: Track The Face  
>The MAKA API can basically perform the functions of face mosaic and face tagging after tracking faces. To track the face, BGR-Type input image is required.    
 
  ```c++
	cv::Mat srcImg;  // source image
	void processMAKAScenario()
	{
		if(srcImg.empty())
			break;
		g_ptrMaka->process(srcImg, MAKA_MODE::FACE_TRACKING, std::vector<_MAKA_DATA_>());  // srcImage should be BGR-type.
	}
  ```  
  ![](./img/tracking_mode.PNG){: width="500"}
* __Step2__: Set to The Face Mosaic  
>To apply the face mosaic, the coordinates within the face selected by a user or its rectangle information obtained from face tracking should be set.
 
  ```c++
	cv::Mat srcImg;
	void processMAKAScenario()
	{
		/*
			// MAKA SCENARIO LOGIC
			.. FACE TRACKING ..
		*/
		g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_SETTING_MOSAIC, makaData);  //the makaData is input data including the face rectangle.
	}
	CALLBACK MouseEvent(Point mosePoint, eventType event) // callback function from external event(mouse or touch)
	{
		std::vector<_MAKA_DATA_> makaData;
		_MAKA_DATA_ data;
		data.faceCenterPos = mousePoint;
		makaData.pushback(data);
		if(event == MOUSE_L_DOWN)
		{
			g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_SETTING_MOSAIC, makaData); // the makaData is input data including the coordinates of event.
		}  	
	}
  ```  
  
* __Step3__: Designate The Face Label  
>To label the face, the rectangle information of the face should be set.
 
  ```c++
	cv::Mat srcImg;
	void processMAKAScenario()
	{
		/*
			// MAKA SCENARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSAIC ..
		*/
	}
	CALLBACK MouseEvent(Point mosePoint, eventType event)
	{
		std::vector<_MAKA_DATA_> makaData;
		_MAKA_DATA_ data;
		data.faceCenterPos = mousePoint;
		makaData.pushback(data);
		if(event == MOUSE_l_DOUBLE_CLICK)
		{
			g_ptrMaka->process(srcImg, MAKA_MODE::FACE_SETTING_LABEL, makaData); // the makaData is input data including the coordinates of event.
		}
	}
  ```  
  
* __Step4__: Apply to The MAKA API with mosaic and tagging 
>The results of the face mosaic and tagging can be obtained from "FACE_GETTING_RESULT" mode.
 
  ```c++
	cv::Mat dispImg;
	cv::Mat srcImg;
	void processMAKAScenario()
	{
		/*
			// MAKA SCENARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSACIC ..
			.. FACE TAGGING ..
		*/
		dispImg = srcImg.clone(); // Copy source image to display image.
		g_ptrMaka->process(dispImg, MAKA_MODE::FACE_GETTING_RESULT, makaData); // dispImg is applied by mosaic, makaData is output data including the face rectangle, face center position, and face label.
		for(std::vector<_MAKA_DATA_>::iterator it = makaData.begin(); it != makaData.end(); it++)
		{
			std::stringsteam strLabel;
			strLabel << it->nLabel;
			cv::putText(dispImg, strLabel.str(), it->faceCenterPos, CV_FONT_HERSHEY_PLAIN, 1, 0, cv::Scalar::all(255)); // display the label.
			cv::rectangle(dispImg, t->faceRect, cv::Scalar(255, 63, 63)); // display the face rectangle. 
		}	
		cv::imshow("display", dispImg);
	} 
  ```  
  ![](./img/mosaic_and_tagging.PNG){: width="500"}
* __Step5__: Release The Face Mosaic  
>If you want to remove the mosaic, enter the coordinates or rectangle of the face.    
 
  ```c++
	void processMAKAScenario()
	{
		/*
			// MAKA SCENARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSACIC ..
			.. FACE TAGGING ..
			.. FACE GET RESULT ..
		*/
		g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_MOSAIC_RELEASE, makaData); // this makaData is input data including the face rectangle.
	}
  
	CALLBACK MouseEvent(Point mosePoint, eventType event)
	{
		std::vector<_MAKA_DATA_> makaData;
		_MAKA_DATA_ data;
		data.faceCenterPos = mousePoint;
		makaData.pushback(data);
		if(event == MOUSE_R_DOWN)
		{
			g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_MOSAIC_RELEASE, makaData); // the makaData is input data including the coordinates of event.
		}
	}
  ```
  
* __Step6__: Release The Face Label  
>If you want to remove the label, enter the coordinates or rectangle of the face.    
 
  ```c++
	void processMAKAScenario()
	{
		/*
			// MAKA SCENARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSACIC ..
			.. FACE TAGGING ..
			.. FACE GET RESULT ..
			.. FACE RELEASE MOSAIC ..
		*/
	}
	CALLBACK MouseEvent(Point mosePoint, eventType event)
	{
		std::vector<_MAKA_DATA_> makaData;
		_MAKA_DATA_ data;
		data.faceCenterPos = mousePoint;
		makaData.pushback(data);  
		if(event == MOUSE_R_DOUBLE_CLICK)
		{
			g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_RELEASE_LABEL, makaData); // the makaData is input data including the coordinates of event.
		}  	
	}
  ```
  
* __Step7__: Release Face Tracking  
>Release of the face tracking will automatically release the face mosaic and face tagging.
 
  ```c++
	void processMAKAScenario()
	{
		/*
			// MAKA SCENARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSACIC ..
			.. FACE TAGGING ..
			.. FACE GET RESULT ..
			.. FACE RELEASE MOSAIC ..
			.. FACE RELEASE LABEL ..
		*/	
		g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_TRACKING_RELEASE, std::vector<_MAKA_DATA_>()); 
	}
  ```
  ![](./img/no_mode.PNG){: width="500"}
* Summary  
>In this guide you have learned the MAKA API by c++ with opencv.  
If you have question, please send an email(hoohyun.kim@deepixel.xyz).  
For more information about API, please refer to the API reference [MAKA API][maka_api].

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
