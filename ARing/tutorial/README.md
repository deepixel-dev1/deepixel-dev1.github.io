![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}

# ARing

>ARing은 얼굴의 귀 정보를 추정하여 어플리케이션 개발자가 응용할 수 있게 해주는 영상처리 기반 API 모듈 입니다.  

***

## 특징

### 1. 귀 위치 추정

> 귀 위치 추정은 사용 유저의 귀를 모바일 상에서 실시간으로 검출해 주는 api 모듈입니다.

![aring display](./img/ARing_Display.png){: width="400"}

- 스팩:
  - 1명만 적용 가능
  - 30~40 FPS (Device: Galaxy Note 8)

- 결과물:  
  - 양쪽 귀의 위치정보, 양쪽 귀 크기, 얼굴 마스크 정보

    ```text
    귀 위치정보: x(이미지의 가로 방향 위치), y(이미지의 세로 방향 위치)
    귀 크기: 귀의 높이 / 영상의 높이
    얼굴 마스크 정보: 얼굴의 바운더리 정보
    ```  

![aring result](./img/ARing_result.png){: width="400"}

***

## 개발 설정

- [Android tutorial][android]

- [iOS tutorial][ios]

***

## ARing API 사용방법 (c++ with OpenCV)

>C++를 이용한 ARing API 사용법을 소개한다. ARing API를 사용하기 위해서는 OpenCV 라이브러리가 필요하다.
ARing API의 예제는 [Android][andoid_sample]/[iOS][ios_sample] 링크를 참고하세요.

1. IARing 객체를 생성한다.

    ```c++
    // Android
    std::shared_ptr<dp::aringnative::IARing> g_ptrARing =
      dp::android::DPFactoryForAndroid::CreateInstance<dp::aringnative::IARing>(
        env, activity);
    ```
    ```c++
    // iOS
    std::shared_ptr<dp::aringnative::IARing> g_ptrARing =
      dp::ios::DPFactoryForiOS::CreateInstance<dp::aringnative::IARing>();
    ```

2. ARing 객체를 초기화 한다.
    > ARing API를 초기화 하기 위해서 다바이스 실행 시 이미지의 크기 및 카메라 센서의 크기, 포커스 값을 입력한다.

    ```c++
    // (img.cols) 이미지 가로 크기
    // (img.rows) 이미지 세로 크기
    // (sensorWidth) 카메라 센서의 가로 크기
    // (sensorHeight) 카메라 센서의 세로 크기
    // (focalLength) 카메라 포커스 값
    g_ptrARing->initialize(img.cols, img.rows, sensorWidth, sensorHeight, focalLength);
    ```

3. 입력 영상의 타입을 gray scale로 변환한다.
    > 디바이스에서 제공하는 영상 타입에 맞는 conversion code를 사용해야 한다.

    ```c++
    cv::Mat matProcessing;
    // (matSRC) 카메라 영상
    // (matProcessing) 변환된 영상
    // (CV_YUV2GRAY_NV21) YUV 영상을 gray scale 영상으로 변경하는 conversion code
    cv::cvtColor(matSRC, matProcessing, CV_YUV2GRAY_NV21);
    ```

4. ARing 객체의 DetectFace 함수를 호출한다.
   > Gray scale 영상을 파라미터로 사용해야 한다. 이 함수의 반환 값에는 얼굴 검출 유무, 귀의 위치 등이 포함되어 있다.

    ```c++
    dp::aringnative::DPAringResult result = g_ptrARing->process(img);
    ```

5. 귀 위치 정보를 이용하여 귀걸이를 출력한다.
    >왼쪽, 오른쪽 귀걸이 2개를 출력한다. 이 때 귀걸이의 크기는 귀의 크기값을 이용해서 결정한다. 귀의 크기는 (귀의 높이 / 입력 영상의 높이)로 결정된다.

    ```c++
    cv::Mat earingPic = cv::imread("earingPic.png"); //귀걸이 영상 입력
    if(result.detected)
    {
      drawEarring(matDST, earingPic, result, true); // LEFT
      drawEarring(matDST, earingPic, result, false); // RIGHT
    }
    ```

    ```c++
    void drawEarring(cv::Mat dst, cv::Mat earingPic, DPARingResult result, bool isLeft)
    {
      float fEaring_scale_y = isLeft ? result.earringScaleYLeft : earringScaleYRight;
      cv::Mat matEarring;
      cv::resize(earingPic, matEarring, cv::Size(0, 0), fEaring_scale_y, fEaring_scale_y);
  
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

      // 얼굴을 일정 각도 이상 돌리면 mask를 적용하지 않는다.
      if (isLeft) {
        if (!(result.rotX < -15))
          matEarring.setTo(0, result.mask(earingRect));
      } else {
        if (!(result.rotX > 15))
          matEarring.setTo(0, result.mask(earingRect));
      }
  
      cv::Mat earingMask;
      cv::compare(matEarring, 0, earingMask, cv::CMP_GT);
  
      // dst의 타입(디바이스에서 영상을 출력할 때 사용하는 타입)으로 변경한다.
      cv::cvtColor(earingMask, earingMask, CV_BGR2RGBA);
      cv::cvtColor(matEarring, matEarring, CV_BGR2RGBA);
  
      cv::subtract(dst(earingRect), earingMask, dst(earingRect));
      cv::add(matEarring, dst(earingRect), dst(earingRect));
    }
  
    ```

***

## 연락처

>API 적용시 문제점을 발견하거나 궁금한 점이 있으면 메일(hoohyun.kim@deepixel.xyz)로 문의해 주세요.  

***

## 참조

- [Anroid Sample][andoid_sample]
- [iOS Sample][ios_sample]
- [ARing API][ARing_api]
- [OpenCV][opencv]
- [TBB][tbb]

[andoid_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/ARing/tutorial/android/
[ios_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/ARing/tutorial/ios
[opencv]: http://opencv.org/
[ARing_api]: https://deepixel-dev1.github.io/ARing/apis/
[tbb]: https://www.threadingbuildingblocks.org/
[android]: android.md
[iOS]: ios.md
