
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

## HOW TO START [MAKA API][maka_api] (For Android)
![](./img/Logic.png){: width="300"}   

#### 1. Insert "Library files" into the library directory that matches the project's target hardware

#### 2. Insert "Include files" into your project's include directory.

#### 3. Create the IMaka Object at the location you want to use.

  ```
  std::shared_ptr<dp::makanative::IMaka> g_ptrMaka;
  g_ptrMaka = dp::android::DPFactoryForAndroid::CreateInstance<dp::makanative::IMaka>(env,
                                                                                    activity,
                                                                                    licenseFilename);
  ```
#### 4. Before the MAKA API is executed, you must call the initialize function of the IMaka.

  ```
  // (img.cols) is size of image width
  // (img.rows) is size of image height
  g_ptrMaka->initialize(img.cols, img.rows); 
  ```
#### 5. To run the MAKA API, call the process function of IMaka.
  ```
  // All generation of MAKA API are excuted by process function in IMaka
  // Parameters(img and _MAKA_DATA_) except MAKA_MODE has both input and output properties
  g_ptrMaka->process(img, MAKA_MODE, _MAKA_DATA_);
  ```
  
*****  
## Example: HOW TO USE MAKA API (c++ with OpenCV)
>This guide will demonstrate how to use MAKA API(with face tracking, mosaic, taggig) based on scenario. The samples are written in c++ using the OpenCV.

* Scenario squence
	1. Track The Face
	2. Apply to The Face Mosaic
	3. Designate The Face Label
	4. Acqurie Result of The MAKA API
	5. Release The Face Mosaic
	6. Release The Face Label
	7. Release The Face Tracking
	
* Step1: Track The Face  
The MAKA API can basically perform mosaic and tagging only if there is face information detected through face tracking. To track the face, BGR-Type input image is required.    
 
  ```
  cv::Mat srcImg;  // source image
  void processMAKASceanario() 
  {
		if(srcImg.empty())
  			break;
  		
  		std::vector<_MAKA_DATA_> makaData;	
  		g_ptrMaka->process(srcImg, MAKA_MODE::FACE_TRACKING, std::vector<_MAKA_DATA_>());  // srcImage should be BGR-type.
  }
  ```
  
* Step2: Apply to The Face Mosaic  
As a method of applying the mosaic, there is a method of inserting the information that the rectangle is selected by reuslt of face tracking or the coordinates in rectangle of the face is designated by user.

  ```
  cv::Mat srcImg;
  void processMAKASceanario()
  {  	
		/*
			// MAKA SCEANARIO LOGIC
			.. FACE TRACKING ..
		*/
  	 	
  		g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_SETTING_MOSAIC, makaData);  //the makaData is input data included face rectangle.
  }  
  
  CALLBACK MouseEvent(Point mosePoint, eventType event) // callback function from external event(mouse or touch)
  {
 	 	std::vector<_MAKA_DATA_> makaData;
 	 	_MAKA_DATA_ data;
 	 	data.faceCenterPos = mousePoint;
 	 	makaData.pushback(data);
  	
 	 	if(event == MOUSE_L_DOWN)
 	 	{
 	 		g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_SETTING_MOSAIC, makaData); //the makaData is input data included coordinate by event
  		}  	
  }
  ```

* Step3: Designate The Face Label  
The label is specified by entering the coordinates in the rectangle of the face.

  ```
  cv::Mat srcImg;
  void processMAKASceanario()
  {
  		/*
			// MAKA SCEANARIO LOGIC
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
  			g_ptrMaka->process(srcImg, MAKA_MODE::FACE_SETTING_LABEL, makaData); // the makaData is input data included the coordinate of event.
  		}  	
  }
  ```
* Step4: Acqurie Result of The MAKA API  
When all the functions of the desired MAKA API are executed, data is acquired through the FACE____GETTING____RESULT mode.

  ```
  cv::Mat dispImg;
  cv::Mat srcImg;
  void processMAKASceanario()
  {
  		/*
			// MAKA SCEANARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSACIC ..
			.. FACE TAGGING ..
		*/
			
		dispImg = srcImg.clone(); // Copy source image to display image
	
  		g_ptrMaka->process(dispImg, MAKA_MODE::FACE_GETTING_RESULT, makaData); // dispImg is applied by mosaic, makaData is output data included face rectangle, face center position, face label 
  		for(std::vector<_MAKA_DATA_>::iterator it = makaData.begin(); it != makaData.end(); it++)
    	{
    		std::stringsteam strLabel;
    		strLabel << it->nLabel;
    		cv::putText(dispImg, strLabel.str(), it->faceCenterPos, CV_FONT_HERSHEY_PLAIN, 1, 0, cv::Scalar::all(255)); // display the label 
    		cv::rectangle(dispImg, t->faceRect, cv::Scalar(255, 63, 63)); // display the face rectangle 
    	}  		
    	cv::imshow("display", dispImg);
  } 
  ```

* Step5: Release The Face Mosaic  
If you want to remove the mosaic, enter the coordinates or rectangle of the face.

  ```
  void processMAKASceanario()
  {
  		/*
			// MAKA SCEANARIO LOGIC
			.. FACE TRACKING ..
			.. FACE MOSACIC ..
			.. FACE TAGGING ..
			.. FACE GET RESULT ..
		*/
	
  		g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_MOSAIC_RELEASE, makaData); // this makaData is input data included face rectangle
  }
  
  CALLBACK MouseEvent(Point mosePoint, eventType event)
  {
  		std::vector<_MAKA_DATA_> makaData;
  		_MAKA_DATA_ data;
  		data.faceCenterPos = mousePoint;
  		makaData.pushback(data);
  	
  		if(event == MOUSE_R_DOWN)
  		{
  			g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_MOSAIC_RELEASE, makaData); // this makaData is input data included mouse postion
  		}  	
  }
  ```
  

* Step6: Release The Face Label  
The label release is similarly performed by receiving the coordinates in rectangle of the face.

  ```
  void processMAKASceanario()
  {
  		/*
			// MAKA SCEANARIO LOGIC
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
  			g_ptrMaka->process(cv::Mat(), MAKA_MODE::FACE_RELEASE_LABEL, makaData); // this makaData is input data included mouse point
  		}  	
  }
  ```

* Step7: Release Face Tracking  
If the face tracking mode is released, All other mode will be released.

  ```
  void processMAKASceanario()
  {
  		/*
			// MAKA SCEANARIO LOGIC
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

* Summary  
In this guide you have learned the MAKA API by c++ with opencv. If you have question, please send a email(hoohyun.kim@deepixel.xyz).  
For more information about API, please refer to the API reference [MAKA API][maka_api]
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
