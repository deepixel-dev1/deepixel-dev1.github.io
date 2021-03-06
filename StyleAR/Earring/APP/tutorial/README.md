[![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}](https://deepixel.azurewebsites.net/)

---

# StyleAREarring API Tutorial

**StyleAREarring API**는 입력영상에서 얼굴 특정점 검출, 3D 피팅과 영상분석 등의 **컴퓨터비전 기술**을 이용하여 사용자에게 **실시간 귀걸이 가상착용**(Virtual Try-on) 경험을 주는 모바일 기반 **API Library**입니다.
**StyleAREarring API**에 대한 각각의 설명은 아래 목차의 **링크**를 참조하세요.

[1. StyleAREarring API 기능](#stylearearring-api-기능)

[2. StyleAREarring API 시스템 사양](#stylearearring-api-시스템-사양)

[3. StyleAREarring API 사용방법](#stylearearring-api-사용방법)

---

## StyleAREarring API 기능

**StyleAREarring API**는 [귀걸이 가상착용](#귀걸이-가상착용), [화면 및 귀걸이 보정](#화면-및-귀걸이-보정), [메타데이터 취득](#메타데이터) 등의 기능을 제공하며 구동스팩은 아래와 같습니다.

|        목차        |                                 스팩                                 |
| :----------------: | :------------------------------------------------------------------: |
|   **Platforms**    |                            Android / iOS                             |
|    **Version**     |                                2.1.0                                 |
|    **SDK Size**    |                                ~ 50M                                 |
| **허용 얼굴 각도** |                       yaw ≤ ±80º, pitch ≤ ±45º                       |
|    **가능인원**    |                                 1명                                  |
|      **속도**      |                  20 ~ 40FPS @ 1080p (Garaxy Note 8)                  |
|      **입력**      | [귀걸이 사진, 귀걸이 크기, 귀걸이 핀위치, 입력영상][make_input_data] |
|      **출력**      |                   귀걸이 가상착용 영상, 메타데이터                   |

### 귀걸이 가상착용

**StyleAREarring API**는 영상에서 **귀걸이 위치**를 **추정**하고, 입력된 **귀걸이와 합성**을 통해 **가상착용영상**을 **출력**합니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/StyleAREarringResult.png" width="600"></center> <br/>

※ 귀걸이 가상착용 기능은 **반드시 귀걸이 사진, 귀걸이 실측 크기, 귀걸이 핀위치**를 입력해야 합니다. 입력 데이터를 구성하는 방법은 [링크][make_input_data]를 참조해 주세요.

※ **가상착용영상의 귀걸이**는 **귀걸이 실측크기** 및 **귀걸이 핀위치** 입력에 따라 **자동**으로 **얼굴크기**에 **비례**하게 출력됩니다.

### 화면 및 귀걸이 보정

**StyleAREarring API**는 가상착용결과를 유려하게 보이기 위해 **출력영상**에 **두가지** **필터 알고리즘**을 적용하고 있습니다. 첫번째는 **화면전체**에 필터를 적용하여 **뽀샵효과**를 줄 수 있고, 두번째는 **귀걸이 색깔**이 **얼굴의 톤**에 맞게 변경 할 수 있습니다. 이 기능들은 **ON / OFF** 할 수 있습니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/Output(Filter).png" width="950"></center><br/>

### 메타데이터

**StyleAREarring API**는 입력영상에서 검출된 **얼굴의 특징을 분석**하여 사용자 각각의 **고유정보**(메타데이터)를 출력할 수 있습니다. 이 기능의 사용을 원하시면 [라이센스 발급](<#필수조건(공통)>)시 사용 유무를 판단하여 문의해 주시기 바랍니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/meta_info_3.png" width="660"></center><br/>

※ **메타데이터**는 얼굴 각 부분들의 **색상**(R<sup>Red</sup>G<sup>Green</sup>B<sup>Blue</sup>), **비율**, **좌표** 값을 분석합니다. 자세한 설명은 아래의 표와 같습니다.

|    메타데이터     |                          내용                          |                                     단위                                      |
| :---------------: | :----------------------------------------------------: | :---------------------------------------------------------------------------: |
|   **피부 색상**   |   피부영역에 대한<br> 색상 평균 및 편차를 나타낸다.    |                   BGR순서로 색상정보가 출력<br>범위:0 ~ 255                   |
| **머리카락 색상** | 머리카락영역에 대한<br> 색상 평균 및 편차를 나타낸다.  |                   BGR순서로 색상정보가 출력<br>범위:0 ~ 255                   |
|   **입술 색상**   |   입술영역에 대한<br> 색상 평균 및 편차를 나타낸다.    |                  BGR순서로 색상정보가 출력 <br>범위:0 ~ 255                   |
|    **옷 색상**    | 사용자의 상의에 대한<br> 색상 평균 및 편차를 나타낸다. |                   BGR순서로 색상정보가 출력<br>범위:0 ~ 255                   |
|   **얼굴 비율**   |           얼굴의 가로 세로 비율을 나타낸다.            | 비율: 가로 / (가로 + 세로)<br>범위:0. ~ 1.<br> 가로: 코<->귀<br>세로: 코<->턱 |
|   **얼굴 라인**   |            얼굴라인을 수치적으로 나타낸다.             |     현재의 얼굴라인 영역 / 얼굴라인의 최대 확장가능 영역 <br>범위:0. ~ 1.     |

---

## StyleAREarring API 시스템 사양

**StyleAREarring API**는 **카메라**에 의해 **입력된 영상데이터** **`LIVE`** 를 처리하는 방법과 **사용자**에 의해 **입력된 영상데이터** **`STILL`** 를 처리하는 방법으로 나눠집니다.

- **StyleAREarring API 블록도**
  > StyleAREarring API와 Mobile Application 간의 관계를 나타낸 **블록도** 입니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/StyleAR_Block_Diagram.png" width="800"></center><br/>

※ **`LIVE`** 방법의 구조는 **컨트롤**과 **프로세싱** 파트로 구성되어 있습니다. **컨트롤 파트**는 **카메라 및 view 제어, 귀걸이 정보 제어, 영상 필터링 제어** 등을 하고 있으며 **프로세싱 파트**는 **알고리즘 구동, 메타정보 및 귀걸이 출력** 등을 맡고 있습니다.

※ **`STILL`** 방법의 구조 역시 **컨트롤**과 **프로세싱** 파트로 구성되어 있습니다. **컨트롤 파트**는 **귀걸이 정보 제어, 영상 필터링 제어**를 하고 있으며, **프로세싱 파트**에서는 **알고리즘 구동**과 **귀걸이 및 메타정보 출력** 등을 맡고 있습니다.

- **StyleAREarring API의 state 다이어그램**
  > StyleAREarring API의 기능들을 사용하기 위해서는 아래의 그림과 같이 간단한 state 메카니즘을 따라야 합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/StyleAR_State_Diagram.png" width="1000"></center><br/>

※ **`LIVE`** 방법은 **초기화 및 준비 상태**를 거처 API가 구동이 되면, 프로세싱 상태에서 루프를 돌며 **결과를 지속적으로 출력**합니다. **귀걸이 변경 및 메타정보 출력**도 **프로세싱 상태**에서 수행 할 수 있습니다. 어플리케이션이 정지하거나, API를 구동해제 할 시에는 **프로세싱 상태를 해제**해야 합니다.

※ **`STILL`** 방법은 **초기화 상태**에서 **귀걸이 설정**을 통해 **준비상태**가 되고, **API 구동**을 하면 **프로세싱 및 출력상태**에서 결과를 **한번만 출력**하고 **준비상태**로 다시 돌아갑니다.

---

## StyleAREarring API 사용방법

**StyleAREarring API 사용방법**에서는 입력영상에서 **얼굴**이 있을 시 **귀의 위치**에 **귀걸이가 가상착용**되는 **모바일 어플리케이션**을 만드는 과정을 **`LIVE`** 와 **`STILL`** 방법으로 나눠 설명합니다. 아래의 예제코드와 함께 비교하며 구현하는 것을 추천합니다.

- Android
  - [LIVE 예제코드][android_sample_live]
  - [STILL 예제코드][android_sample_still]
- iOS
  - [LIVE 예제코드][ios_sample_live]
  - [STILL 예제코드][ios_sample_still]

### 필수조건(공통)

어플리케이션을 개발하기 앞서, **StyleAREarring API**를 사용하기 위해서는 **라이센스**를 **발급**받아야 합니다.

- **라이센스 라이브러리 발급**  
  **StyleAREarring API**를 사용하기 위한 **라이센스 라이브러리 발급**은 아래의 표의 이메일로 **문의**하고, 내용은 **폼**에 맞게 **작성**해야 합니다.

|   목차   |                                             내용                                             |
| :------: | :------------------------------------------------------------------------------------------: |
| **발급** |                           <support@deepixel.xyz>로 문의 바랍니다.                            |
|  **폼**  | 1.회사명<br> 2. 적용 어플리케이션 ID<br> 3. StyleAREarring API 사용기간<br> 4. 담당자 연락처 |

- **환경설정**  
  환경설정에서는 **StyleAREarring API 라이브러리**를 **모바일 프로젝트**에 **등록하는 방법** 및 **프로젝트 권한 설정** 방법을 설명합니다.
  - [Android 환경 설정][android_tutorial]
  - [iOS 환경 설정][ios_tutorial]

---

### StyleAREarring API 적용방법 (`LIVE`)

**StyleAREarring API `LIVE`** 를 적용하는 방법을 아래의 **Flow 다이어그램**의 순서대로 설명합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/StyleAR_Sample_Flow(LIVE).png" width="500"></center><br/>

※ 우선 **StyleAREarring API `LIVE`** 를 동작시키기 위해서는 `(1) StyleAREarring view UI 컴포넌트를 생성`하고 `(2) 시작함수를 호출`해야 합니다. **StyleAREarring API** **동작 중**에는 `(2-1) 귀걸이 설정`, `(2-2) 메타데이터 취득`과 `(2-3) 필터 설정`을 할 수 있습니다. **StyleAREarring API `LIVE`** 는 `(3) 종료함수 호출`을 통해 구동을 멈출 수 있습니다. 자세한 설명은 아래와 같습니다.

- **(1) StyleAREarring view UI 컴포넌트 생성**

  **StyleAREarring API `LIVE`** 는 **카메라와 View를 컨트롤** 하는 기능을 **내부**에 **포함**하고 있습니다. 따라서 **StyleAREarring view UI 컴포넌트**를 화면에 추가하고 일부 설정만 하면 쉽게 사용할 수 있습니다.

  - Android

    - StyleAREarring view UI 컴포넌트 생성
      애플리케이션 **Layout**에 **StyleAREarring view UI 컴포넌트**를 추가합니다.

      ```xml
      <!--For Android-->
      <xyz.deepixel.stylear.earring.DPStyleAREarringView
        android:id="@+id/stylear_earring_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentStart="true"
        android:layout_alignParentTop="true"
        android:layout_marginStart="0dp"
        android:layout_marginTop="0dp"
        android:background="#FFFFFFFF">
      </xyz.deepixel.stylear.earring.DPStyleAREarringView>
      ```

    - StyleAREarring view 객체 가져오기  
      먼저 사용할 클래스를 import 합니다.

      ```java
      // For android
      // StyleAREarring API LIVE Camera 및 view 컨트롤 클래스
      import xyz.deepixel.stylear.earring.DPStyleAREarringView;
      // StyleAREarring API 귀걸이 정보 파라메터 설정 클래스
      import xyz.deepixel.stylear.earring.DPEarringParam;
      // StyleAREarring API 메터다이터 정보저장 클래스
      import xyz.deepixel.stylear.earring.DPFaceMetaData;
      ```

      StyleAREarring view 의 id를 이용하여 객체를 가져옵니다.

      ```java
      // For Android
      // StyleAREarring View 객체 선언
      private DPStyleAREarringView m_stylearView
      // StyleAREarring View 클래스를 layout의 view에 연결
      m_stylearView = view.findViewById(R.id.stylear_earring_view); //연결
      ```

  - iOS

    - StyleAREarring view UI 컴포넌트 생성  
      StyleAREarring view 객체를 생성하고 추가한다.

      ```objective-c
      // For iOS
      #import <StyleAREarring/StyleAREarring.h>
      ```

      ```objective-c
      // For iOS
      // StyleAREarring View 객체 생성
      DPStyleAREarringView *styleAREarringView = [DPStyleAREarringView alloc] initWithFrame:frame.bounds];
      // StyleAREarring View 객체를 부모 View에 추가
      [frame addSubView:styleAREarringView];
      ```

- **(2) 구동**  
  **StyleAREarring API `LIVE`** 동작을 시작합니다. **StyleAREarring view UI 컴포넌트**에 결과 영상을 출력합니다.

  ```java
  // For Android
  m_stylearView.start();
  ```

  ```objective-c
  // For iOS
  [styleAREarringView start];
  ```

  - **(2-1) 귀걸이 설정**
    귀걸이 설정은 **StyleAREarring API `LIVE`** **동작 중**에도 할 수 있습니다. 귀걸이를 설정하기 위해서는 **귀걸이 영상**, **귀걸이의 크기**와 **핀 위치** 정보가 필요합니다. 귀걸이 영상을 만드는 방법은 [링크][make_input_data]를 참조하세요.

    - Android

      ```java
      // For Android
      // StyleAREarring API 귀걸이 객체 생성
      DPEarringParam earringParam = new DPEarringParam();
      // Bitmap 타입설정 (반드시 RGB_8888)
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      // 귀걸이 Bitmap 영상 설정
      earringParam.setBitmap(BitmapFactory.decodeFile(mEarringFile.getAbsolutePath(), options));
      // 실제 귀걸이 크기(width, height)
      earringParam.setSize(SizeF size);
      // 귀걸이 핀 위치(귀걸이 위에서 핀까지의 거리)
      earringParam.setAnchorPoint(PointF size);
      // StyleAREarring API LIVE에 좌측 귀걸이 정보 입력
      m_stylearView.setLeftEarringParam(earringParam);
      // StyleAREarring API LIVE에 우측 귀걸이 정보 입력
      m_stylearView.setLeftEarringParam(earringParam);
      ```

    - iOS

      ```objective-c
      // For iOS
      // StyleAREarring API 귀걸이 객체 생성
      DPEarringParam *earringParam = [[DPEarringParam alloc] init];
      // 귀걸이 CGImageRef 영상 설정
      earringParam.cgImage = GetImgRef(@"earring", @".png");
      // 실제 귀걸이 크기(width, height)
      earringParam.size = CGSizeMake(20.0f, 30.0f);
      // 귀걸이 핀 위치(귀걸이 위에서 핀까지의 거리)
      earringParam.anchorPoint = CGPointMake(10.0f, 1.0f);
      // StyleAREarring API LIVE에 좌측 귀걸이 정보 입력
      [_styleAREarringView setLeftEarringParam:earringParam];
      // StyleAREarring API LIVE에 우측 귀걸이 정보 입력
      [_styleAREarringView setRightEarringParam:earringParam];
      ```

  - **(2-2) 메타데이터 정보 획득**
    **StyleAREarring API `LIVE`** 가 동작하는 동안 **얼굴**에 대한 다양한 **메타 정보를 획득** 할 수 있습니다. 사용자가 **정면**을 바라보고 있을 때만 메타데이터가 업데이트 됩니다.

    - Android

      ```java
      // For Android
      // 메타데이터 획득
      DPFaceMetaData faceMetaData = m_stylearView.getFaceMetaData();
      StringBuilder msg = new StringBuilder();
      // 얼굴 비율 출력
      msg.append("FRM : ").append(faceMetaData.getFaceRatioMean()).append('\n');
      // 얼굴 라인 비율 출력
      msg.append("CRM : ").append(faceMetaData.getChinLineRatioMean()).append('\n');
      // 머리카락 색깔 평균 출력
      msg.append("HCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getHairColorMean())).append('\n');
      // 머러카락 색깔 편차 출력
      msg.append("HCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getHairColorStd())).append('\n');
      // 입술 색깔 평균 출력
      msg.append("LCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getLipColorMean())).append('\n');
      // 입술 색깔 편차 출력
      msg.append("LCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getLipColorStd())).append('\n');
      // 피부색 색깔 평균 출력
      msg.append("SCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getSkinColorMean())).append('\n');
      // 피부색 색깔 편차 출력
      msg.append("SCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getSkinColorStd())).append('\n');
      // 옷 색깔 평균 출력
      msg.append("DCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getDressColorMean())).append('\n');
      // 옷 색깔 편차 출력
      msg.append("DCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getDressColorStd()));
      ```

    - iOS

      ```objective-c
      // For iOS
      // 메타데이터 획득
      DPFaceMetaData *faceMetaData = [_styleAREarringView getFaceMetaData];
      NSMutableString *str = [[NSMutableString alloc] init];
      // 얼굴 비율 평균 출력
      [str appendFormat:@"FRM = %f\n", faceMetaData.faceRatioMean];
      // 얼굴 비율 편차 출력
      [str appendFormat:@"CRM = %f\n", faceMetaData.chinLineRatioMean];
      // 머리카락 색깔 평균 출력
      [str appendFormat:@"HCM = #%06X\n", 0xFFFFFF & faceMetaData.hairColorMean];
      // 머러카락 색깔 편차 출력
      [str appendFormat:@"HCS = #%06X\n", 0xFFFFFF & faceMetaData.hairColorStd];
      // 입술 색깔 평균 출력
      [str appendFormat:@"LCM = #%06X\n", 0xFFFFFF & faceMetaData.lipColorMean];
      // 입술 색깔 편차 출력
      [str appendFormat:@"LCS = #%06X\n", 0xFFFFFF & faceMetaData.lipColorStd];
      // 피부색 색깔 평균 출력
      [str appendFormat:@"SCM = #%06X\n", 0xFFFFFF & faceMetaData.skinColorMean];
      // 피부색 색깔 편차 출력
      [str appendFormat:@"SCS = #%06X\n", 0xFFFFFF & faceMetaData.skinColorStd];
      // 옷 색깔 평균 출력
      [str appendFormat:@"DCM = #%06X\n", 0xFFFFFF & faceMetaData.dressColorMean];
      // 옷 색깔 편차 출력
      [str appendFormat:@"DCS = #%06X", 0xFFFFFF & faceMetaData.dressColorStd];
      ```

  - **(2-3) 필터설정**
    **StyleAREarring API `LIVE`** 의 출력결과에 화면 및 귀걸이 보정필터 유무(**ON/OFF**) 설정 합니다. **기본 설정값**은 **OFF**(false) 입니다.

    - Android

      ```java
      // For Android
      // 화면 보정필터 ON / OFF
      m_stylearView.setFilterEnabled(filterEnabled);
      // 귀걸이 보정필터 ON / OFF
      m_stylearView.setEarringFilterEnabled(earringFilterEnabled);
      ```

    - iOS

      ```objective-c
      // For iOS
      // 화면 보정필터 ON / OFF
      [_styleAREarringView setFilterEnabled:enabled];
      // 귀걸이 보정필터 ON / OFF
      [_styleAREarringView setEarringFilterEnabled:enabled];
      ```

- **(3) 정지**  
   **StyleAREarring API `LIVE`** 동작을 정지합니다. **StyleAREarring API `LIVE`** 가 설정되어 있는 **UI 컴포넌트**에 **결과 영상**을 **출력**하는 것을 **멈춥니다**.

  ```java
  // For Android
  m_stylearView.stop();
  ```

  ```objective-c
  // For iOS
  [styleAREarringView stop];
  ```

---

### StyleAREarring API 적용방법(`STILL`)

**StyleAREarring API `STILL`** 을 적용하는 방법을 아래의 **Flow 다이어그램**의 순서대로 설명합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Earring/APP/tutorial/img/StyleAR_Sample_Flow(STILL).png" width="150"></center><br/>

※ **StyleAREarring API `STILL`** 를 구동하기 위해서 우선 `(1) 인스턴스를 생성하고` `(2) 귀걸이를 설정합니다.` 그리고 `(3) 필터 사용 유무`를 설정하고, `(4) 결과`를 얻을 수 있습니다. 추가적으로 `(5) 메타데이터`도 얻을 수 있습니다. 자세한 설명은 아래와 같습니다.

- **(1) 인스턴스 생성**  
   **StyleAREarring API`STILL`** 를 구동시키기위한 **API 인스턴스**를 **생성** 합니다.

  - 클래스 import  
    **StyleAREarring API`SITLL`** 을 구동하기 위한 클래스를 import 합니다.

    - Android

      ```java
      // StyleAREarring API STILL 컨트롤 클래스
      import xyz.deepixel.stylear.earring.DPStyleAREarring;
      // StyleAREarring API 귀걸이 설정 클래스
      import xyz.deepixel.stylear.earring.DPEarringParam;
      ```

    - iOS

      ```objective-c
      #import <StyleAREarring/StyleAREarring.h>
      ```

  - 함수호출  
    **StyleAREarring API `STILL`** **인스턴스**를 **생성** 합니다.

    - Android

      ```java
      // Context activity: StyleAREarring API STILL이 포함된 activity를 입력
      DPStyleAREarring styleAR = DPStyleAREarring.getInstance(Context activity);
      ```

    - iOS

      ```objective-c
      DPStyleAREarring *styleAREarring = [DPStyleAREarring sharedInstance];
      ```

- **(2) 귀걸이 설정**  
   **StyleAREarring API `STILL`** 이 **구동하기 이전**에 **귀걸이** 을 설정해야 합니다. **귀걸이 설정방법**은 **StyleAREarring API `LIVE`** 와 **동일**합니다.

  - Android

    ```java
    // For Android
    // StyleAREarring API LIVE와 설정방법이 동일
    //    ...
    //    ...
    //    ...
    // StyleAREarring API STILL 에 좌측 귀걸이 정보 입력
    styleAR.setLeftEarringParam(leftEarringParam);
    // StyleAREarring API STILL 에 우측 귀걸이 정보 입력
    styleAR.setRightEarringParam(rightEarringParam);
    ```

  - iOS

    ```objective-c
    // For iOS
    // StyleAREarring API LIVE와 설정방법이 동일
    //    ...
    //    ...
    //    ...
    // StyleAREarring API STILL 에 좌측 귀걸이 정보 입력
    [styleAREarring setLeftEarringParam:leftEarringParam];
    // StyleAREarring API STILL 에 우측 귀걸이 정보 입력
    [styleAREarring setRightEarringParam:rightEarringParam];
    ```

- **(3) 필터설정**  
   **StyleAREarring API `STILL`** 의 **출력영상**에 **화면 및 귀걸이 보정필터** 적용 유무(**ON/OFF**)를 설정합니다. **기본 설정값**은 **OFF**(false)입니다.

  - Android

    ```java
    // For Android
    // 화면 보정필터 ON / OFF
    styleAR.setFilterEnabled(filterEnabled);
    // 귀걸이 보정필터 ON / OFF
    styleAR.setEarringFilterEnabled(earringFilterEnabled);
    ```

  - iOS

    ```objective-c
    // For iOS
    // 화면 보정필터 ON / OFF
    [styleAREarring setFilterEnabled:filterEnabled];
    // 귀걸이 보정필터 ON / OFF
    [styleAREarring setEarringFilterEnabled:earringFilterEnabled];
    ```

- **(4) 구동 및 출력**  
  **StyleAREarring API `STILL`** 에 **영상**을 입력하면 **가상착용된 결과 영상**을 **출력**합니다.

  - Android

    ```java
    // For android
    // Bitmap bitmap: Bitmap 타입의 입력영상
    // Bitmap styleARImage: 귀걸이가 가상착용된 출력영상
    Bitmap styleARImage = styleAR.getStyleARImage(Bitmap bitmap);
    ```

  - iOS

    ```objective-c
    // For iOS
    // CGImageRef cgImage: CGImageRef 타입의 입력영상
    // CGImageRef styleARImage: 귀걸이가 가상착용된 출력영상
    CGImageRef styleARImage = [styleAREarring getStyleARImage:cgImage];
    ```

- **(5) 메타데이터 획득**  
   **StyleAREarring API `STILL`** 이 **구동된 이후**에 **메타데이터**를 **획득**할 수 있습니다. **메타데이터 출력 방법**은 **StyleAREarring API `LIVE`** 와 **동일**합니다.

  - Android

    ```java
    // For Android
    // 메타데이터 정보획득
    DPFaceMetaData faceMetaData = styleAR.getFaceMetaData();
    // 출력방법은 StyleAREarring API LIVE와 동일
    //  ...
    //  ...
    //  ...
    ```

  - iOS

    ```objective-c
    // For iOS
    // 메타데이터 정보획득
    DPFaceMetaData *faceMetaData = [styleAREarring getFaceMetaData];
    // 출력방법은 StyleAREarring API LIVE와 동일
    //  ...
    //  ...
    //  ...
    ```

---

## 연락처

StyleAREarring API 적용시 문제점을 발견하거나 궁금한 점이 있다면, 이메일(support@deepixel.xyz)로 문의해 주세요.

---

## 참조

- Android
  - [Android Sample code(LIVE)][android_sample_live]
  - [Android Sample code(STILL)][android_sample_still]
  - [Android 환경 설정][android_tutorial]
  - [StyleAREarring API For Android][stylear_api_for_android]
- iOS
  - [iOS Sample code(LIVE)][ios_sample_live]
  - [iOS Sample code(STILL)][ios_sample_still]
  - [iOS 환경 설정][ios_tutorial]
  - [StyleAREarring API For iOS][stylear_api_for_ios]
- [StyleAREarring API 입력데이터 만들기][make_input_data]
- [딥픽셀 홈페이지][deepixel_hompage]

[android_sample_live]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/Earring/APP/tutorial/android/StyleAREarringForAndroidSample(LIVE)
[android_sample_still]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/Earring/APP/tutorial/android/StyleAREarringForAndroidSample(STILL)
[license]: /License/README.md
[android_tutorial]: /StyleAR/Earring/APP/tutorial/android
[stylear_api_for_android]: /StyleAR/Earring/APP/apis/android/index.html
[ios_sample_live]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/Earring/APP/tutorial/ios/StyleAREarringForiOSSample/StyleAREarringSample-Live
[ios_sample_still]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/Earring/APP/tutorial/ios/StyleAREarringForiOSSample/StyleAREarringSample-Still
[ios_tutorial]: /StyleAR/Earring/APP/tutorial/ios
[stylear_api_for_ios]: /StyleAR/Earring/APP/apis/ios/index.html
[make_input_data]: /StyleAR/Earring/APP/tutorial/input
[deepixel_hompage]: https://www.deepixel.xyz/
