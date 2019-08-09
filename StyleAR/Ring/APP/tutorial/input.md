[![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}](https://deepixel.azurewebsites.net/)

***

# StyleARRing API 입력 데이터

**StyleARRing API**를 사용하여 손에 반지가 가상착용된 영상을 출력하기 위해서는 먼저 **투명 처리된 반지 영상, 가이드 영상, 착용할 반지의 손가락 위치 등**을 입력하여 초기화 한 후에, API가 동작할 때에는 **손이 포함된 입력영상**을 입력해야 동작할 수 있습니다.

입력 데이터를 구성하는 자세한 방법 설명은 아래 **링크** 를 참조하세요.

 [1. 가이드 영상 입력](#가이드-영상-입력)

 [2. 반지 영상 입력](#반지-영상-입력)

 [3. 반지 위치 입력](#반지-위치-입력)

 [4. 영상 입력](#영상-입력)

 ***

## 가이드 설정 입력

**가이드 영상**은 **StyleARRing API**가 원할히 동작하기 위해 **사용자 손 입력**을 **제한**하기 위해 설정해야 하며, 개발자가 원하는 영상을 입력할 수 있습니다. 가이드 영상은 **배경**은 모두 **투명처리** 하며, **가이드 손**에 대한 것은 **알파 값**(투명도)를 조정한 영상을 입력해야 합니다. **가이드 템플릿 크기**는 **카메라**에서 **인식**하려는 **손의 크기**에 맞게 조절 할 수 있습니다. 템플릿 크기 파라메터(**디펄트 5**)의 값은 **10 ~ 0** 이며 **10**은 인식하려는 손이 크기가 **최대치** 일때, **0**은 인식하려는 손의 크기가 **최소치** 일때 입니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Input(guide).png" width="400"></center> <br/>

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Input(guide2).png" width="700"></center> <br/>

※ **가이드 영상**은 **손이 꽉차게** 만들어야 합니다.

## 반지 영상 입력

반지 영상은 반드시 **투명 바탕**으로 된 **png타입**이고, **반지 영상이 최대한 꽉차게** 만들어야 합니다. 반지는 옆면이 조금 보이도록 촬영하고, 반지영상은 아래와 같은 작업을 통해 생성합니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Input(ring).png" width="700"></center> <br/>

※ 반지 사진을 만들 시 **고화질 영상**으로 작업하시면 더욱 좋은 결과를 출력 할 수 있으며, 투명 사진은 포토샵, [파워포인트][remove_background_ppt] 등의 툴을 이용하시기 바랍니다.

## 반지 위치 입력

**반지 위치 입력**은 **손 선택**, **손가락 위치 선택**, **손 마디 위치 선택**을 통해 반지의 위치를 정해줄 수 있습니다. **손 선택**은 **왼손과 오른손** 중 하나를 선택하며, 선택된 손을 기준으로 API가 동작합니다. **손가락 위치 선택**은 엄지를 제외한 **가상착용을 원하는 손가락**을 선택합니다. **손가락 마디** 위치 선택은 마디의 어느 위치(범위: 0. ~ 1.)에 착용알고리즘을 적용할 것인지 선택하는 것입니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Input(position).png" width="800"></center> <br/>

※ **손가락 위치 선택**은 **복수 선택**이 가능하며, 선택된 손가락 각각에 반지 영상을 입력해야 합니다.

## 영상 입력

**StyleARRing API**은 **손가락 특징점** **검출 및 분석**을 기반으로 알고리즘이 동작하기 때문에 반드시 입력 영상에는 **손이 포함**되어야 합니다. **가이드**에 따라 손이 영상내에 입력되면 알고리즘이 동작할 수 있습니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Input(Image).png" width="700"></center> <br/>

※ 가이드에 따라 손이 검출되어 **알고리즘이 동작**하면, 그 후의 손에 대한 영상입력은 **크기와 포즈**에 어느정도 **자유도**를 가질 수 있다.

## 참조

- [배경지우는 방법(PPT)][remove_background_ppt]
- [StyleAREarring API 튜토리얼][android_tutorial]

[remove_background_ppt]: https://support.office.com/en-us/article/remove-the-background-of-a-picture-c0819a62-6844-4190-8d67-6fb1713a12bf
[android_tutorial]: https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/
