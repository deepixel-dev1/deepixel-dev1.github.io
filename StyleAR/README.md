![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}

# StyleAR Tutorial

>StyleAR은 모바일 장치로 부터 입력된 영상에서 얼굴특정 위치 검출, 3D 피팅, 영상분석등을 통해 귀걸이 착용영상과 메타정보를 실시간으로 출력하는 컴퓨터 비전 기반의 API Library 입니다.  

![aring display](./img/face(earring).png){: width="200"}

***

## 특징

### 귀걸이 착용

> 영상내의 사용자의 귀를 모바일 장치에서 실시간으로 검출하여 양쪽 귀에 귀걸이가 착용된 영상을 제공합니다. 이것을 이용해서 온라인상에서 사용자가 귀걸이를 가상으로 착용(Virtual Try-on)할 수 있는 서비스를 만들 수 있습니다.

- 스팩:

    |목차|스팩|비고|
    |:-:|:-:|:-:|
    |가능인원|1명||
    |동작범위||범위 내|
    |속도| FPS (Frame Per Secound) |실험 장비(Galaxy Note 8), 해상도(1920 X 1080)|

- 입력물:

    |목차|내용|비고|
    |:-:|:-:|:-:|
    |얼굴영상|![aring result](./img/face.jpg){: width="200"}|얼굴이 사진의 40%이상 차지|
    |귀걸이 사진|![aring result](./img/input_1.png){: width="300"}|1. 영상편집 시 귀걸이 위와 아래의 공백을 최대한 작게 <br />2. 영상은 귀걸이를 제외한 배경은 투명처리(png 포멧)|
    |귀걸이 크기|1. 귀걸이 실측 가로크기 <br />2. 귀걸이 실측 세로크기|단위(mm)|

- 결과물:

    |결과물|내용|비고|
    |:-:|:-:|:-:|
    |귀걸이 착용 영상|![deepixel.xyz](./img/faceResult.png){: width="200"}|귀걸이 실측크기에 맞게 배치|
    |메타정보|![deepixel.xyz](./img/meta_info.png){: width="500"}| 색상정보 범위(0 ~ 255) <br /> 얼굴비율 범위 (0 ~ 1) |


***

## 환경설정

- [Android 환경설정][android]

- [iOS 환경설정][ios]

***

## StyleAR API 사용방법 (c++ with OpenCV)

>C++를 이용한 StyleAR API 사용법을 소개한다. StyleAR API를 사용하기 위해서는 OpenCV 라이브러리가 필요합니다.
StyleAR API의 전체 예제 코드는 [Android][andoid_sample]/[iOS][ios_sample] 링크를 참고합니다.

## 연락처

>API 적용시 문제점을 발견하거나 궁금한 점이 있다면, 다음의 이메일(hoohyun.kim@deepixel.xyz)로 문의해 주세요.  

***

## 참조

- [Anroid Sample][andoid_sample]
- [iOS Sample][ios_sample]
- [ARing API][ARing_api]
- [OpenCV][opencv]
- [TBB][tbb]

[andoid_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/AR1ing/tutorial/android/
[ios_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/AR1ing/tutorial/ios
[opencv]: http://opencv.org/
[ARing_api]: /AR1ing/apis/
[result]: /AR1ing/apis/namespacedp_1_1ar1ingnative.html
[image_type]: /AR1ing/apis/namespacemembers_enum.html
[tbb]: https://www.threadingbuildingblocks.org/
[android]: android.md
[iOS]: ios.md
[license]: /License/README.md
