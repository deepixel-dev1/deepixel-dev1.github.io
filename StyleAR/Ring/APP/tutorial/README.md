[![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}](https://deepixel.azurewebsites.net/)

***

# StyleARRing API Tutorial

**StyleARRing API**는 **입력영상**에서 **손 특징점 검출** 및 **영상분석** 등의 **컴퓨터비전 기술**을 이용하여 사용자에게 **실시간 반지 가상착용**(Virtual Try-on)경험을 주는 모바일 기반 **API Library**입니다.<br>  
**StyleARRing API**에 대한 각각의 설명은 아래 목차의 **링크**를 참조하세요.

 [1. StyleARRing API 기능](#stylearring-api-기능)

 [2. StyleARRing API 시스템 사양](#stylearring-api-시스템-사양)

 [3. StyleARRing API 사용방법](#stylearring-api-사용방법)

 ***

## StyleARRing API 기능

**StyleARRing API**는 [반지 가상착용](#반지-가상착용), [메타데이터 취득](#메타데이터) 등의 기능을 제공하며 구동스팩은 아래와 같습니다.

|목차              |스팩                                                              |
|:-:               |:-:                                                              |
|**Platforms**     |Android                                                          |
|**Version**       |1.2.1                                                            |
|**SDK Size**      | ~ 25M                                                           |
|**가능인원**      |1명                                                               |
|**속도**          | 20 ~ 40FPS @ 1080p (Garaxy Note 8)                              |
|**입력**          | [반지 영상, 반지위치, 입력영상, 가이드 영상][make_input_data]|
|**출력**          | 반지 가상착용 영상, 메타데이터                                   |

### 반지 가상착용

**StyleARRing API**는 영상에서 **손 위치**를 **추정**하고, 입력된 **반지와 합성**을 통해 **가상착용영상**을 **출력**합니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Output(HandImg).png" width="600"></center> <br/>

※ 반지 가상착용은 입력으로 반드시 **손이 포함된 입력영상**, **반지영상**, **반지위치**, **가이드영상**을 입력해야 합니다. 입력데이터는 링크를 클릭해주세요.

### 메타데이터

**StyleARRing API**는 입력영상에서 **검출된 손의 특징을 분석**하여 사용자 각각의 **고유정보**(메타데이터)를 출력할 수 있습니다. 이 기능의 사용을 원하시면 [라이센스 발급](#필수조건(공통))시 사용 유무를 판단하여 문의해 주시기 바랍니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Output(MetaINFO).png" width="330"></center><br/>

※ **메타데이터**는 손의 크기 및 비율, 색상 값을 분석합니다. 자세한 설명은 아래의 표와 같습니다.

|메타데이터|내용|단위|
|:-:|:-:|:-:|
|**피부 색상**      |손의 피부색을 나타낸다.|RGB순서로 색상정보가 출력<br>범위:0 ~ 255                                            |
|**손톱 색상**  |손톱 색을 나타낸다.|RGB순서로 색상정보가 출력<br>범위:0 ~ 255                                               |
|**손 가락 길이**   |손 가락 길이를 나타낸다.|손 가락의 끝 마디에서 손톱까지의 세로길이를 나태낸다. <br>범위: 스크린 크기 내                            |
|**손 가락 너비** |손 가락 너비를 나타낸다.|손가락 마디의 가로길이를 나타낸다. <br>범위: 스크린 크기 내                              |
|**손 가락 비율**      | 손 바닥 길이 대비 가운데 손가락 길이의 비율을 나타낸다.|비율: 손가락 길이 / (손가락 길이 + 손바닥 길이)<br>범위:0. ~ 1.|

***

## StyleARRing API 시스템 사양

**StyleARRing API**는 모바일 카메라에서 입력된 영상데이터에 영상처리 알고리즘을 적용하여 사용자에게 반지 가상착용을 제공하는 API 입니다.

- **StyleARRing API 블록도**
  >StyleARRing API와 Mobile Application 간의 관계를 나타낸 **블록도** 입니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Block(API).png" width="500"></center><br/>

※ **StyleARRing API**는 **컨트롤**과 **프로세싱** 파트로 구성되어 있습니다. **컨트롤 파트**에서 **카메라 및 view 제어, 반지 정보 제어, 영상 필터링 제어** 등을 하고 있으며 **프로세싱 파트**는 **알고리즘 구동, 메타정보 및 반지 가상착용 영상 출력** 등을 맡고 있습니다.

※ **카메라 입력과 반지 가상착용 결과 영상출력**을 **API 내부**에서 처리합니다. 따라서 개발 시 **모바일환경**에 대한 **설정없이** **UI에 API를 호출**하는 방법으로 간단히 모바일 어플리케이션을 구성할 수 잇습니다. 

- **StyleARRing API의 state 다이어그램**
  >StyleARRing API의 기능들을 사용하기 위해서는 아래의 그림과 같이 간단한 state 메카니즘을 따라야 합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Block(state).png" width="600"></center><br/>

※ **StyleARRing API**는 **초기화 및 준비 상태**를 거처 API가 구동이 되면, 프로세싱 상태에서 루프를 돌며 **결과를 지속적으로 출력**합니다. **반지 변경 및 메타정보 출력**도 **프로세싱 상태**에서 수행 할 수 있습니다. 어플리케이션이 정지하거나, API를 구동해제 할 시에는 **프로세싱 상태를 해제**해야 합니다.  

***

## StyleARRing API 사용방법

**StyleARRing API 사용방법**에서는 API 구동 필수조건 및 사용방법등에 대해 설명하고 있으며, 아래의 예제코드와 함께 비교하며 이해하는 것을 추천합니다.

- [LIVE 예제코드][android_sample_live]
- [STILL 예제코드][android_sample_still]

### 필수조건(공통)

어플리케이션을 개발하기 앞서, **StyleARRing API**를 사용하기 위해서는 **라이센스**를 통해 **발급**받은 **라이브러리 파일**이 있어야 하며, 라이브러리 파일을 개발 프로젝트에 사용하기 위한 **설정**이 필요합니다.

- **라이센스 라이브러리 발급**  
  **StyleARRing API**를 사용하기 위한 **라이센스 라이브러리 발급**은 아래의 표의 이메일로 **문의**하고, 내용은 **폼**에 맞게 **작성**해야 합니다.

|목차|내용|
|:-:|:-:|
|**발급**|<support@deepixel.xyz>로 문의 바랍니다.|
|**폼**|1.회사명<br> 2. 적용 어플리케이션 ID<br> 3. StyleARRing API 사용기간<br> 4. 메타데이터기능 사용유무<br> 5. 담당자 연락처 |

- **환경설정**  
  환경설정에서는 **StyleARRing API 라이브러리**를 **모바일 프로젝트**에 **등록하는 방법** 및 **프로젝트 권한 설정** 방법을 설명합니다.
  - [Android 환경 설정][android_tutorial]

### StyleARRing API 적용방법 (`LIVE`)

**StyleARRing API `LIVE`** 를 적용하는 방법을 아래의 **Flow 다이어그램**의 순서대로 설명합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/Ring/APP/tutorial/img\Flow(LIVE).png" width="500"></center><br/>

※ **StyleARRing API**는 우선 `(1)StyleARRing view UI 컴포넌트 연결`을 하고 `(2)시작함수 호출`을 통해 동작시킵니다. **동작 중**에는 `(2-1, 2-2)반지 설정`, `(2-3)가이드 설정`, `(2-4)메타데이터 취득`을 할 수 있고, `(3)종료함수 호출`을 통해 구동을 멈출 수있습니다. 자세한 설명은 아래와 같습니다.

- **(1) StyleARRing view UI생성 및 연결**  
  **StyleARRing API** 는 **카메라 및 view를 컨트롤** 하는 기능을 **내부**에 **포함**하고 있습니다. 따라서 어플리케이션 **Layout**에 **StyleARRing view UI 컴포넌트**를 만들고, **StyleARRing view 클래스**를 연결하는 코드만 추가하면 **다른 설정 필요없이 사용**할 수 있습니다.

  - **StyleARRing view UI 컴포넌트 생성**  
    **Main Layout**에 **StyleARRing view UI 컴포넌트**를 생성합니다.

    ```xml
    <!--For Android-->
    <xyz.deepixel.stylear.DPStyleARRingView
      android:id="@+id/stylear_ring_view"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:layout_alignParentStart="true"
      android:layout_alignParentTop="true"
      android:layout_marginStart="0dp"
      android:layout_marginTop="0dp"
      android:background="#FFFFFFFF">
    </xyz.deepixel.stylear.DPStyleARringView>
    ```

  - **StyleARRing view 연결**  
    먼저 사용할 **클래스**를 **import** 합니다.

    ```java
    // StyleARRing API Camera 및 view 컨트롤 클래스
    import xyz.deepixel.stylear.ring.DPStyleARRingView;
    // StyleARRing API 반지 정보 파라메터 설정 클래스
    import xyz.deepixel.stylear.ring.DPRingParam;
    // StyleARRing API 메터다이터 정보저장 클래스
    import xyz.deepixel.stylear.ring.DPHandMetaData;
    ```

  - **StyleARRing view 클래스를 StyleARRing view UI 컴포넌트에 연결**

    ```java
    // For Android
    // StyleARRing View Controller 선언
    private DPStyleARRingView m_stylearView
    // StyleARRing View 클래스를 layout의 view에 연결  
    m_stylearView = view.findViewById(R.id.stylear_ring_view); //연결
    ```

- **(2) 구동**  
    **StyleARRing API** 동작을 시작합니다. **StyleARRing view UI 컴포넌트**에 결과 영상을 출력합니다.

    ```java
    // For Android
    m_stylearView.start();
    ```

  - **(2-1) 반지 영상 설정**  
      반지 영상 설정은 **Bitmap** 형식의 **반지영상데이터**를 설정하고 싶은 손가락에 입력해야 합니다. 반지영상을 만드는 방법은 [반지 입력데이터 만들기][make_input_data]의 **반지영상** 파트를 참조해 주시기 바랍니다.

      ```java
      // For Android
      // StyleARRing API 반지 정보 클래스 선언
      DPRingParam ringParam = new DPRingParam();
      BitmapFactory.Options options = new BitmapFactory.Options();
      // Bitmap 타입설정 (반드시 RGB_8888)
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      // 반지 Bitmap영상 설정
      ringParam.setBitmap(BitmapFactory.decodeFile(mRingFile.getAbsolutePath(), options));
      // nFingerID는 반지를 설정하고 싶은 손가락 번호(0 => 엄지, 1 => 검지, 2 => 중지, 3=> 약지, 4=> 새끼)
      int nFingerID = 3;
      // StyleARRing API 반지 영상 설정
      m_stylearView.setRingParam(ringParam, nFingerID);
      ```

  - **(2-2) 반지 위치 설정**  
      반지 위치 설정은 **손 방향 선택, 손가락 위치 선택, 반지 위치 선택**을 입력해야 합니다. 각 입력에 대한 내용은 [반지 입력데이터 만들기][make_input_data]의 **반지위치입력** 파트를 참조해 주십시오.

      ```java
      // For Android
      // 착용할 반지 손가락 위치 (다중선택 가능)
      boolean[] bFingers = {false, true, true, false, false};
      // 착용할 반지 손 방향 선택(true => 왼쪽, false => 오른쪽)
      boolean IsLeft = true;
      // 착용할 반지 위치 선택(범위: 0. ~ 1.)
      float ringOffset = 0.5;
      // StyleARRing API 반지 위치 설정
      m_stylearView.setProcessRingParam(bFingers, IsRight, ringOffset);
      ```

  - **(2-3) 가이드 설정**  
      가이드 설정은 어플리케이션에 **가이드 사용유무**를 확인하고 사용 시 가이드 영상을 입력(Bitmap)합니다. 만약 사용하지 않을 시에도 가이드 영상을 따로 만들어 사용하는 것을 추천합니다. 가이드 설정에 대한 입력은 [반지 입력데이터 만들기][make_input_data]의 **가이드 영상 파트**를 참조해 주세요.

      ```java
      // For Android
      // isEnable true=> 사용 함, false => 사용 안함
      boolean isEnable = true;
      // StyleARRing API에 가이드 사용 유무 설정.
      m_stylearView.setHandTemplateEnable(isEnable);
      BitmapFactory.Options options = new BitmapFactory.Options();
      // Bitmap 타입설정 (반드시 RGB_8888)
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      // 가이드 Bitmap영상 설정 (가이드 사용안함이면 적용 안됨)
      m_stylearView.setHandTemplate(BitmapFactory.decodeFile(mGuideFile.getAbsolutePath(), options));
      ```

  - **(2-4) 메타데이터 정보 획득**  
      **StyleARRing API** 가 동작하는 동안 **손**에 대한 다양한 **메타 정보를 획득** 할 수 있습니다.

      ```java
      // For Android
      //메타데이터 정보획득
      DPHandMetaData handMetaData = m_stylearView.getHandMetaData();
      StringBuilder msg = new StringBuilder();
      // 손과 손바닥 비율
      msg.append("FIN : ").append(handMetaData.getfinger2palmRatio() ).append('\n');
      // 손가락 길이
      // nFinger => 출력을 원하는 손가락
      msg.append("LENGTH : ").append(handMetaData.getFingerLength(nFinger) ).append('\n');
      // 손가락 너비
      // nFinger => 출력을 원하는 손가락
      msg.append("WIDTH : ").append(handMetaData.getFingerWidth(nFinger) ).append('\n');
      // 손톱 칼라 평균
      msg.append("NCM : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getNailColorMean())).append('\n');
      // 손톱 칼라 표준오차
      msg.append("NCS : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getNailColorStd())).append('\n');
      // 손 스킨 칼라 평균
      msg.append("SCM : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getSkinColorMean())).append('\n');
      // 손 스킨 칼라 표준오차
      msg.append("SCS : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getSkinColorStd())).append('\n');
      ```

- **(3) 정지**  
    **StyleARRing API** 동작을 정지합니다. **StyleARRing API** 가 설정되어 있는 **UI 컴포넌트**에 **결과 영상**을 **출력**하는 것을 **멈춥니다**.

    ```java
    // For Android
    m_stylearView.stop();
    ```

***

## 연락처

StyleARRing API 적용시 문제점을 발견하거나 궁금한 점이 있다면, 이메일(support@deepixel.xyz)로 문의해 주세요.  

***

## 참조

- [Android Sample code][android_sample_live]
- [Android 환경 설정][android_tutorial]
- [StyleARRing API For Android][stylear_api_for_android]
- [StyleARRing API 입력데이터 만들기][make_input_data]
- [딥픽셀 홈페이지][deepixel_hompage]

[android_sample_live]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/Ring/APP/tutorial/android/StyleARRingForAndroidSample(LIVE)
[license]: /License/README.md
[android_tutorial]: /StyleAR/Earring/APP/tutorial/android
[stylear_api_for_android]: /StyleAR/Ring/APP/apis/android
[make_input_data]: /StyleAR/Ring/APP/tutorial/input
[deepixel_hompage]: https://deepixel.azurewebsites.net/