![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}

***

# StyleAR API 입력 데이터 만들기

StyleAR API를 사용하여 결과를 출력하기 위해서는 **카메라 영상, 귀걸이 사진, 귀걸이 실측 사이즈**를 입력해야 합니다.

아래의 목차는 입력 데이터를 만드는 방법을 구분하여 **링크**하였습니다.

 [1. 영상 입력](#영상-입력)

 [2. 귀걸이 사진 입력](#귀걸이-사진-입력)

 [3. 귀걸이 실측 크기 입력](#귀걸이-실측-크기-입력)

 ***

## 영상 입력

StyleAR API는 영상처리 알고리즘을 통해 얼굴을 찾고, 귀걸이의 위치를 추정합니다. 따라서 입력 영상에는 **얼굴이 포함** 되어야 하며 **얼굴의 크기는 영상의 크기에 40% 이상**이 되어야 합니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/Input(Face).png" width="300">
</center>

영상입력 조건: Input<sup>Area</sup> × 0.4 < Face<sup>Area</b></sup> 

## 귀걸이 사진 입력

StyleAR API의 입력으로 귀걸이 사진을 입력 시에는 반드시 **투명 바탕화면**으로 된 **png 확장자 타입**으로 만들어야 합니다. 또한 **최대한 귀걸이가 전체 사진화면에 꽉차게** 만들어야 합니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/Input(Earring).png" width="450"></center> <br/>

※ 귀걸이 사진을 만들 시 **고화질 영상**으로 작업하시면 더욱 좋은 결과를 출력 할 수 있으며, 투명 사진은 포토샵, 파워포인트 등의 툴을 이용하시기 바랍니다.

## 귀걸이 실측 크기 입력

StyleAR API는 **귀걸이의 실측 크기(단위: mm)를 이용하여 얼굴크기에 맞게 가상착용** 합니다. 따라서 실측크기를 정확하게 입력해야 자연스러운 출력영상을 얻을 수 있습니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/Input(EarringSize).png" width="350"></center> <br/> 

***

## 참조

- [배경지우는 방법(PPT)][remove_background_ppt]
- [StyleAR API 튜토리얼][android_tutorial]

[remove_background_ppt]: https://support.office.com/en-us/article/remove-the-background-of-a-picture-c0819a62-6844-4190-8d67-6fb1713a12bf
[android_tutorial]: https://deepixel-dev1.github.io/StyleAR/tutorial/
