
![](./img/Deepixel_logo.PNG){: width="200"}  

ARing API TUTORIAL (For Android)
=========================

>ARing은 얼굴의 귀 정보를 추정하여 어플리케이션 개발자가 응용할 수 있게 해주는 영상처리 기반 API 모듈 입니다.  

***

## 특징

### 1. 귀 위치 추정

> 귀 위치 추정은 사용 유저의 귀를 모바일 상에서 실시간으로 검출해 주는 api 모듈입니다. 
 
![](./img/FT_SS.png){: width="400"}  

* 스팩:
	* 1명만 적용 가능
	* 30~40 FPS (Device: Galaxy Note 8)
 
* 결과물:  
	* 귀의 왼쪽, 오른쪽 위치정보, 귀 스케일, 얼굴 마스크 정보  	
	
	```
	귀 위치정보: x(이미지의 가로 방향 위치), y(이미지의 세로 방향 위치)
	귀 스케일: 얼굴 크기에 따른 귀의 스케일 정보
	얼굴 마스크 정보: 얼굴의 바운더리 정보
    ```  

![](./img/FacePosition.png){: width="400"}	


*****
## ARing API 파일
 * ARing API(C++)  
   
   ```
   ARing/include/opencv_3.3.1/                 : OpenCV library folder
   ARing/include/IARing.h                      : ARing Interface Header file
   ARing/include/DPException.h                 : ARing Exception Header file
   ARing/include/DPFactoryForAndroid.h         : ARing Singleton Header file
   ARing/libs/armeabi-v7a/libopencv_world.so   : OpenCV library file
   ARing/libs/armeabi-v7a/libtbb.so            : tbb library file
   ARing/libs/armeabi-v7a/libaringnative.so    : ARing library file
   ```     
 * ARing Sample(Android)  
 
 ```
   ARing/ARingAndroidExample/..                : ARing Android Sample Code files
 ```    

*****

## [ARing API][ARing_api] 설치방법 및 간단사용법 (For Android)
![](./img/Logic.png){: width="300"}   

#### 1. "라이브러리 파일"을 타켓 하드웨어 프로젝트 라이브러리 디렉토리에 넣는다. 

#### 2. "해더 파일"을 include 디렉토리에 넣는다.

#### 3. IARing 오브젝트를 적용하고 싶은 코드에 생성한다.
  ```
  std::shared_ptr<dp::aringnative::IARing> g_ptrARing;
  g_ptrARing = dp::android::DPFactoryForAndroid::CreateInstance<dp::aringnative::IARing>(env,
                                                                                    activity,
                                                                                    licenseFilename);
  ```
#### 4. ARing API를 실행하기 이전에, 반듯이 ARing initialize 함수를 호출 한다.
  ```
  // (img.cols) 이미지 가로 크기
  // (img.rows) 이미지 세로 크기
  // (sensorWidth) 카메라 센서의 가로 크기
  // (sensorHeight) 카메라 센서의 세로 크기
  // (focalLength) 카메라 포커스 값
  g_ptrMaka->initialize(img.cols, img.rows, sensorWidth, sensorHeight, focalLength); 
  ```
#### 5. ARing DetectFace 함수를 호출하여, ARing API를 동작 시킨다. 
  ```
  // DPAringResult는 ARing API의 결과를 출력한다.
  dp::aringnative::DPAringResult g_ptrARing->process(img);
  ```  
*****  
## 예제: ARing API 사용방법 (c++ with OpenCV)
>아래 시나리오에 따른 ARing API 사용법을 소개한다. 해당 샘플은 C++을 기반으로 하며, OpenCV 라이브러리를 사용해야 한다. 

* Scenario steps  
Step1. ARing API 초기화  
Step2. 동영상 변환  
Step3. 귀 추정 API 실행  
Step4. 귀 위치 및 마스크에 따른 화면 출력  
    
* __Step1__: ARing API 초기화  
> ARing API를 초기화 하기 위해서 다바이스 실행 시 이미지의 크기 및 카메라 센서의 크기, 포커스 값을 입력한다.    
 
  ```c++
	cv::Mat srcImg;  // source image
	void DeviceInitialize()
	{
		int imageWidth, int imageHeight, float sensorWidth, float sensorHeight, float focalLength;
		getDeviceInformation(imageWidth, imageHeight, sensorWidth, sensorHeight, focalLength);
		g_ptrARing->initialize(imageWidth, imageHeight, sensorWidth, sensorHeight, focalLength);
	}
  ```  
* __Step2__: 동영상 변환
> 동영상을 입력은 반드시 입력 포멧을 BGR로 변환해야 한다. 
 
  ```c++  
	/*
		초기화(Step1)
	*/
	cv::Mat srcImg;
	void processARingScenario(cv::Mat matSRC, cv::Mat matDST)
	{
		cv::Mat matProcessing;
		cv::cvtColor(matSRC, matProcessing, CV_현재타입_BGR);			  	
	}
  ```  
  
* __Step3__: 귀 추정 API 실행
>귀 추정 API를 실행 하고 결과 값을 출력한다.
 
  ```c++
	/*
		초기화(Step1)
	*/
	void processARingScenario(cv::Mat matSRC, cv::Mat matDST)
	{
		/*
			동영상 변환(Step2)
		*/
		DPARingResult result;
		result = g_ptrARing->DetecFace(matProcessing);			  	
	}
  ```  
  
* __Step4__: 귀 추정 결과 영상에 출력 
>추정 된 귀에 대한 정보를 바탕으로 귀걸이가 적용된 영상을 저장 및 화면에 출력하고자 하는 영상에 적용한다.
 
  ```c++
	/*
		초기화(Step1)
	*/
	void drawEar(cv::Mat dst, cv::Mat earingPic, DPARingResult result, bool isLeft)
	{
		float fEaring_scale_y = isLeft ? result.earringScaleYLeft : earringScaleYRight;
		cv::Mat matEarring;
		cv::resize(earingPic, matEaring, cv::Size(0, 0), fEaring_scale_y, fEaring_scale_y);
		
		if(matEarring.empty())
		return;
		
		cv::Point earingPt = isLeft ? result.leftEarringPos : result.rightEarringPos;
		earingRect = earingRect + earingPt;
		earingRect = earingRect - cv::Point(earingRect.width / 2, earingRect.width / 4);
		cv::Rect earingRect(0, 0, matEarring.cols, matEarring.rows);
		
		cv::Rect safeRect(0, 0, dst.cols, dst.rows);
		earingRect = earingRect & safeRect;
		
		if(matEarring.size().area()!=earingRect.area())
			return;
			
		matEarring.setTo(0, result.mask, cv::CMP_GT);
		
		cv::cvtColor(earingMask, earingMask, CV_BGR2RGBA); //모바일에서는 RGBA로 변환해야 함
    	cv::cvtColor(matEarring, matEarring, CV_BGR2RGBA);
    	
    	cv::subtract(matDisplay(earingRect), earingMask, matDisplay(earingRect));
		cv::add(matEarring, matDisplay(earingRect), matDisplay(earingRect));
		
	}
	void processARingScenario(cv::Mat matSRC, cv::Mat matDST)
	{
		/*
			동영상 변환(Step2)
			귀 추정 API 실행(Step3)
		*/
		cv::Mat earingPic = cv::imread("earingPic.png"); //귀걸이 영상 입력
		if(result.detected)
		{
			drawEar(matDST, earingPic, result, true); // LEFT
			drawEar(matDST, earingPic, result, false); // RIGHT
		}
	}
  ```  
  
* Summary  
>ARing API 모듈은 반드시 OpenCV 라이브러리를 필요로 하며, C++로 구현 되어야 합니다.  
만약 API 적용시 문제점 발견이나 궁금한 점은 메일(hoohyun.kim@deepixel.xyz)로 보내주세요.  
API함수에 대한 문서는 [ARing API][ARing_api]를 참조 하시기 바랍니다.  

*****

## Reference
- [ARing API][ARing_api]
- [OpenCV][opencv]
- [TBB][tbb]

[opencv]: http://opencv.org/
[ARing_api]: https://deepixel-dev1.github.io/makanative/api/
[tbb]: https://www.threadingbuildingblocks.org/

