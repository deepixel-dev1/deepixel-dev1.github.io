
# ARing API 안드로이드 환경설정

*****  




*****

## [ARing API][ARing_api] 설치방법 및 간단사용법

1. "라이브러리 파일"을 타켓 하드웨어 프로젝트 라이브러리 디렉토리에 넣는다.

2. "해더 파일"을 include 디렉토리에 넣는다.

3. IARing 오브젝트를 적용하고 싶은 코드에 생성한다.

    ```c
    std::shared_ptr<dp::aringnative::IARing> g_ptrARing =
      dp::android::DPFactoryForAndroid::CreateInstance<dp::aringnative::IARing>(
        env, activity);
    ```

4. ARing API를 실행하기 이전에, 반듯이 ARing initialize 함수를 호출 한다.

    ```c
    // (img.cols) 이미지 가로 크기
    // (img.rows) 이미지 세로 크기
    // (sensorWidth) 카메라 센서의 가로 크기
    // (sensorHeight) 카메라 센서의 세로 크기
    // (focalLength) 카메라 포커스 값
    g_ptrMaka->initialize(img.cols, img.rows, sensorWidth, sensorHeight, focalLength);
    ```

5. ARing DetectFace 함수를 호출하여, ARing API를 동작 시킨다. 

    ```c
    // DPAringResult는 ARing API의 결과를 출력한다.
    dp::aringnative::DPAringResult g_ptrARing->process(img);
    ```

*****

## 예제: ARing API 사용방법 (c++ with OpenCV)

>아래 시나리오에 따른 ARing API 사용법을 소개한다. 해당 샘플은 C++을 기반으로 하며, OpenCV 라이브러리를 사용해야 한다.

* __Step1__: ARing API 초기화
  
  > ARing API를 초기화 하기 위해서 다바이스 실행 시 이미지의 크기 및 카메라 센서의 크기, 포커스 값을 입력한다.

  ```c++
  cv::Mat srcImg;  // source image
  void DeviceInitialize()
  {
    int imageWidth, imageHeight;
    float sensorWidth, sensorHeight, focalLength;
    getDeviceInformation(imageWidth, imageHeight, sensorWidth, sensorHeight, focalLength);
    g_ptrARing->initialize(imageWidth, imageHeight, sensorWidth, sensorHeight, focalLength);
  }
  ```

* __Step2__: 동영상 변환
  > 동영상을 입력은 반드시 입력 포멧을 BGR로 변환해야 한다. 

  ```c++  
  cv::Mat srcImg;
  void processARingScenario(cv::Mat matSRC, cv::Mat matDST)
  {
    // 동영상 변환(Step2)
    cv::Mat matProcessing;
    cv::cvtColor(matSRC, matProcessing, CV_현재타입_BGR);
  }
  ```

* __Step3__: 귀 추정 API 실행
  >귀 추정 API를 실행 하고 결과 값을 출력한다.

  ```c++
  void processARingScenario(cv::Mat matSRC, cv::Mat matDST)
  {
    // 동영상 변환(Step2)
    ...
    // 귀 추정 API 실행(Step3)
    DPARingResult result = g_ptrARing->DetecFace(matProcessing);
  }
  ```

* __Step4__: 귀 추정 결과 영상에 출력
  >추정 된 귀에 대한 정보를 바탕으로 귀걸이가 적용된 영상을 저장 및 화면에 출력하고자 하는 영상에 적용한다.

  ```c++
  void drawEar(cv::Mat dst, cv::Mat earingPic, DPARingResult result, bool isLeft)
  {
    float fEaring_scale_y = isLeft ? result.earringScaleYLeft : earringScaleYRight;
    cv::Mat matEarring;
    cv::resize(earingPic, matEaring, cv::Size(0, 0), fEaring_scale_y, fEaring_scale_y);

    if(matEarring.empty())
      return;

    cv::Point earingPt = isLeft ? result.leftEarringPos : result.rightEarringPos;

    cv::Rect earingRect(0, 0, matEarring.cols, matEarring.rows);

    earingRect = earingRect + earingPt;
    earingRect = earingRect - cv::Point(earingRect.width / 2, earingRect.width / 4);

    cv::Rect safeRect(0, 0, dst.cols, dst.rows);
    earingRect = earingRect & safeRect;

    if(matEarring.size().area()!=earingRect.area())
      return;

    if (isLeft) {
      if (!(result.rotX < -15))
        matEarring.setTo(0, result.mask(earingRect));
    } else {
      if (!(result.rotX > 15))
        matEarring.setTo(0, result.mask(earingRect));
    }

    cv::Mat earingMask;
    cv::compare(matEarring, 0, earingMask, cv::CMP_GT);

    cv::cvtColor(earingMask, earingMask, CV_BGR2RGBA); //모바일에서는 RGBA로     변환해야 함
    cv::cvtColor(matEarring, matEarring, CV_BGR2RGBA);

    cv::subtract(matDisplay(earingRect), earingMask, matDisplay(earingRect));
    cv::add(matEarring, matDisplay(earingRect), matDisplay(earingRect));
  }

  void processARingScenario(cv::Mat matSRC, cv::Mat matDST)
  {
    // 동영상 변환(Step2)
    ...
    // 귀 추정 API 실행(Step3)
    ...
    // 귀 추정 결과 영상에 출력
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
ARing API의 안드로이드 샘플 코드는 [ARing_Sample][aring_sample]를 참조해 주세요. 

*****

## Reference

* [ARing_Sample][aring_sample]
* [ARing API][ARing_api]
* [OpenCV][opencv]
* [TBB][tbb]

[aring_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/ARing/tutorial/android/
[opencv]: http://opencv.org/
[ARing_api]: https://deepixel-dev1.github.io/makanative/api/
[tbb]: https://www.threadingbuildingblocks.org/