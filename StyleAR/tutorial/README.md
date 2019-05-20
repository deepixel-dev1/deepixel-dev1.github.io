[![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}](https://deepixel.azurewebsites.net/)

***

# StyleAR API Tutorial

**StyleAR API**는 입력영상에서 얼굴 특정점 검출, 3D 피팅, 영상분석 등의 **컴퓨터비전 기술**을 이용하여 사용자에게 **실시간 귀걸이 가상착용**(Virtual Try-on)경험을 주는 모바일 기반 **API Library**입니다.<br>  
**StyleAR API**에 대한 각각의 설명은 아래 목차의 **링크**를 참조하세요.

 [1. StyleAR API 기능](#stylear-api-기능)

 [2. StyleAR API 시스템 사양](#stylear-api-시스템-사양)

 [3. StyleAR API 사용방법](#stylear-api-사용방법)

 ***

## StyleAR API 기능

**StyleAR API**는 [귀걸이 가상착용](#귀걸이-가상착용), [화면보정](#화면보정), [메타데이터 취득](#메타데이터) 등의 기능을 제공하며 구동스팩은 아래와 같습니다.

|목차              |스팩                                                              |
|:-:               |:-:                                                              |
|**Platforms**     |Android                                                          |
|**Version**       |1.1.4                                                            |
|**SDK Size**      | ~ 20M                                                           |
|**허용 얼굴 각도** |yaw ≤ ±80º, pitch ≤ ±45º                                         |
|**가능인원**      |1명                                                               |
|**속도**          | 20 ~ 40FPS @ 1080p (Garaxy Note 8)                              |
|**입력**          | [귀걸이 사진, 귀걸이 크기, 귀걸이 핀위치, 입력영상][make_input_data]|
|**출력**          | 귀걸이 가상착용 영상, 메타데이터                                   |

### 귀걸이 가상착용

**StyleAR API**는 영상에서 **귀걸이 위치**를 **추정**하고, 입력된 **귀걸이와 합성**을 통해 **가상착용영상**을 **출력**합니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAREarringResult.png" width="600"></center> <br/>

※ 귀걸이 가상착용 기능은 **반드시 귀걸이 사진, 귀걸이 실측 크기, 귀걸이 핀위치**를 입력해야 합니다. 입력 데이터를 구성하는 방법은 [링크][make_input_data]를 참조해 주세요.

※ **가상착용영상의 귀걸이**는 **귀걸이 실측크기** 및 **귀걸이 핀위치** 입력에 따라 **자동**으로 **얼굴크기**에 **비례**하게 출력됩니다.

### 화면보정

**StyleAR API**는 가상착용결과를 유려하게 보이기 위해 **출력영상**에 **필터 알고리즘**을 적용하고 있습니다. 이 기능은 **ON / OFF** 가 가능합니다.

### 메타데이터

**StyleAR API**는 입력영상에서 검출된 **얼굴의 특징을 분석**하여 사용자 각각의 **고유정보**(메타데이터)를 출력할 수 있습니다. 이 기능의 사용을 원하시면 [라이스 발급](#필수조건)시 사용 유무를 판단하센여 문의해 주시기 바랍니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/meta_info.png" width="330"></center><br/>

※ **메타데이터**는 얼굴 각 부분들의 **색상**(R<sup>Red</sup>G<sup>Green</sup>B<sup>Blue</sup>), **비율**, **좌표** 값을 분석합니다. 자세한 설명은 아래의 표와 같습니다.

|메타데이터|내용|단위|
|:-:|:-:|:-:|
|**피부 색상**      |얼굴의 피부색을 나타낸다.|RGB순서로 색상정보가 출력<br>범위:0 ~ 255                                            |
|**머리카락 색상**  |머리카락 색을 나타낸다.|RGB순서로 색상정보가 출력<br>범위:0 ~ 255                                               |
|**입술 색상**      |입술 색을 나타낸다.|RGB순서로 색상정보가 출력 <br>범위:0 ~ 255                                                 |
|**왼쪽 귀 좌표**   |왼쪽 귀의 좌표를 나타낸다.|스크린의 원점을 기준으로 x, y 좌표<br>범위: 스크린 크기 내                            |
|**오른쪽 귀 좌표** |오른쪽 귀의 좌표를 나타낸다.|스크린의 원점을 기준으로 x, y 좌표<br>범위: 스크린 크기 내                              |
|**얼굴 비율**      | 얼굴의 가로 세로 비율을 나타낸다.|비율: 가로 / (가로 + 세로)<br>범위:0. ~ 1.<br> 가로: 코<->귀<br>세로: 코<->턱   |

***

## StyleAR API 시스템 사양

**StyleAR API**는 카메라에 의해 입력된 영상데이터 **`LIVE`** 를 처리하는 방법과 사용자에 의해 입력된 영상데이터 **`STILL`** 를 처리하는 방법으로 나눠집니다.

- **StyleAR API 블록도**
  >StyleAR API와 Mobile Application 간의 관계를 나타낸 **블록도** 입니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_Block_Diagram.png" width="800"></center><br/>

※ **`LIVE`** 방법의 구조는 **컨트롤**과 **프로세싱** 파트로 구성되어 있습니다. **컨트롤 파트**는 **카메라 및 view 제어, 귀걸이 정보 제어, 영상 필터링 제어** 등을하고 있으며 **프로세싱 파트**는 **알고리즘 구동, 메타정보 및 귀걸이 출력** 등을 맡고 있습니다.

※ **`STILL`** 방법의 구조 역시 **컨트롤**과 **프로세싱** 파트로 구성되어 있습니다. **컨트롤 파트**는 **귀걸이 정보 제어, 영상 필터링 제어**를 하고 있으며, **프로세싱 파트**에서는 **알고리즘 구동**과 **귀걸이 및 메타정보 출력** 등을 맡고 있습니다.

- **StyleAR API의 state 다이어그램**
  >StyleAR API의 기능들을 사용하기 위해서는 아래의 그림과 같이 간단한 state 메카니즘을 따라야 합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_State_Diagram.png" width="1000"></center><br/>

※ **`LIVE`** 방법은 **초기화 및 준비 상태**를 거처 API가 구동이 되면, 프로세싱 상태에서 루프를 돌며 **결과를 지속적으로 출력**합니다. **귀걸이 변경 및 메타정보 출력**도 **프로세싱 상태**에서 수행 할 수 있습니다. 어플리케이션이 정지하거나, API를 구동해제 할 시에는 **프로세싱 상태를 해제**해야 합니다.  

※ **`STILL`** 방법은 **초기화 상태**에서 **귀걸이 설정**을 통해 **준비상태**가 되고, **API 구동**을 하면 **프로세싱 및 출력상태**에서 결과를 **한번만 출력**하고 **준비상태**로 다시 돌아갑니다.

***

## StyleAR API 사용방법

**StyleAR API 사용방법**에서는 입력영상에서 **얼굴**이 있을 시 **귀의 위치**에 **귀걸이가 가상착용**되는 **모바일 어플리케이션**을 만드는 과정을 **`LIVE`** 와 **`STILL`** 방법으로 나눠 설명합니다. 아래의 예제코드와 함께 비교하며 구현하는 것을 추천합니다.

- [LIVE 예제코드][android_sample_live]
- [STILL 예제코드][android_sample_still]

### 필수조건(공통)

어플리케이션을 개발하기 앞서, **StyleAR API**를 사용하기 위해서는 **라이센스**를 통해 **발급**받은 **라이브러리 파일**이 있어야 하며, 라이브러리 파일을 개발 프로젝트에 사용하기 위한 **설정**이 필요합니다.

- **라이센스 라이브러리 발급**  
  **StyleAR API**를 사용하기 위한 **라이센스 라이브러리 발급**은 아래의 표의 이메일로 **문의**하고, 내용은 **폼**에 맞게 **작성**해야 합니다.

|목차|내용|
|:-:|:-:|
|**발급**|<support@deepixel.xyz>로 문의 바랍니다.|
|**폼**|1.회사명<br> 2. 적용 어플리케이션 ID<br> 3. StyleAR API 사용기간<br> 4. 메타데이터기능 사용유무<br> 5. 담당자 연락처 |

- **환경설정**  
  환경설정에서는 **StyleAR API 라이브러리**를 **모바일 프로젝트**에 **등록하는 방법** 및 **프로젝트 권한 설정** 방법을 설명합니다.
  - [Android 환경 설정][android_tutorial]

### StyleAR API 적용방법 (`LIVE`)

**StyleAR API `LIVE`** 를 적용하는 방법을 아래의 **Flow 다이어그램**의 순서대로 설명합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_Sample_Flow(LIVE).png" width="500"></center><br/>

※ 우선 **StyleAR API `LIVE`** 동작하기 위한 준비단계로는 `(1)StyleAR view UI 컴포넌트 연결`하고 실제 구동 위해 `(2)시작함수 호출`을 해야 합니다. **StyleAR API** **동작 중**에는 `(2-1)귀걸이 변경` 및 `(2-2)메타데이터 취득`을 할 수 있습니다. **StyleAR API `LIVE`** 은 `(3)종료함수 호출`을 통해 구동을 멈출 수있습니다. 자세한 설명은 아래와 같습니다.

- **(1) StyleAR view UI생성 및 연결**  
  **StyleAR API `LIVE`** 는 입력영상을 카메라에서 얻을 수도 있습니다. 하지만 **카메라 설정에 따라 잘못된 결과를 출력** 할 수 있기 때문에, **StyleAR API `LIVE`** 는 **카메라 및 view를 컨트롤** 하는 기능을 **내부**에 **포함**하고 있습니다. 따라서 **StyleAR API `LIVE`** 를 사용 할 시 Layout에 StyleAR view UI 컴포넌트를 만들고 실제 StyleAR view 클래스를 연결하는 코드만 추가하면 **다른 설정 필요없이 사용**할 수 있습니다.

  - StyleAR view UI 컴포넌트 생성  
    Main Layout에 StyleAR view UI 컴포넌트를 생성합니다.

    ```xml
    <!--For Android-->
    <xyz.deepixel.stylear.DPStyleAREarringView
      android:id="@+id/stylear_view"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:layout_alignParentStart="true"
      android:layout_alignParentTop="true"
      android:layout_marginStart="0dp"
      android:layout_marginTop="0dp"
      android:background="#FFFFFFFF">
    </xyz.deepixel.stylear.DPStyleAREarringView>
    ```

  - StyleAR view 연결  
    먼저 사용할 클래스를 import 합니다.

    ```java
    // StyleAR API LIVE Camera 및 view 컨트롤 클래스
    import xyz.deepixel.stylear.DPStyleAREarringView;
    // StyleAR API 귀걸이 정보 파라메터 설정 클래스
    import xyz.deepixel.stylear.DPEarringParam;
    // StyleAR API 메터다이터 정보저장 클래스
    import xyz.deepixel.stylear.DPFaceMetaData;
    ```

    StyleAR view 클래스를 Layout의 StyleAR view UI 컴포넌트에 연결합니다.

    ```java
    // For Android
    // StyleAR View Controller 선언
    private DPStyleAREarringView m_stylearView
    // StyleAR View 클래스를 layout의 view에 연결  
    m_stylearView = view.findViewById(R.id.stylear_view); //연결
    ```

- **(2) 구동**  
    **StyleAR API `LIVE`** 동작을 시작합니다. **StyleAR view UI 컴포넌트**에 결과 영상을 출력합니다.

    ```java
    // For Android
    m_stylearView.start();
    ```

  - **(2-1) 귀걸이 설정**  
      귀걸이 변경은 **StyleAR API `LIVE`** **동작 중**에도 할 수 있으며, 적용과 동시에 귀걸이가 변하는 것을 볼 수 있습니다. 귀걸이를 설정하기 위해서는 우측과 좌측의 **귀걸이 영상**(bitmap)과 **귀걸이의 크기** 그리고 **핀 위치**)가 필요합니다. 입력데이터를 만드는 방법은 [링크][make_input_data]를 참조하세요.

      ```java
      // For Android
      // StyleAR API 귀걸이 정보 클래스 선언
      DPEarringParam earringParam = new DPEarringParam();
      // 1. 귀걸이 사진 파일 위치 (사진 파일(1) 또는 Bitmap(2) 형식으로 선택함.)
      earringParam.setAbsolutePath(mEarringFile.getAbsolutePath());
      // 2. 귀걸이 Bitmap 설정 (사진 파일(1) 또는 Bitmap(2) 형식으로 선택함.)
      BitmapFactory.Options options = new BitmapFactory.Options();
      // Bitmap 타입설정 (반드시 RGB_8888)
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      // 귀걸이 Bitmap영상 설정
      earringParam.setBitmap(BitmapFactory.decodeFile(mEarringFile.getAbsolutePath(), options));
      // 실제 귀걸이 크기(width, height)
      earringParam.setSize(SizeF size);
      // 귀걸이 핀 위치(귀걸이 위에서 핀까지의 거리)
      earringParam.setAnchorPoint(PointF size);
      // StyleAR API LIVE에 좌측 귀걸이 정보 입력
      m_stylearView.setLeftEarringParam(earringParam);
      // StyleAR API LIVE에 우측 귀걸이 정보 입력
      m_stylearView.setLeftEarringParam(earringParam);
      ```

  - **(2-2) 메타데이터 정보 획득**  
      **StyleAR API `LIVE`** 이 동작하는 동안 카메라 입력 영상에서의 **얼굴**에 대한 다양한 **메타 정보를 획득** 할 수 있으며, 사용자의 **얼굴**이 **카메라에 정면**을 바라보고 있을 때 **동작** 합니다.

      ```java
      // For Android
      //메타데이터 정보획득
      DPFaceMetaData faceMetaData = m_stylearView.getFaceMetaData();
      StringBuilder msg = new StringBuilder();
      // 얼굴 비율 평균 출력
      msg.append("FRM : ").append(faceMetaData.getFaceRatioMean()).append('\n');
      // 얼굴 비율 편차 출력
      msg.append("FRS : ").append(faceMetaData.getFaceRatioStd()).append('\n');
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
      ```

  - **(2-3) 필터설정**  
      **StyleAR API `LIVE`** 의 출력결과에 화면보정 필터의 ON/OFF 기능을 설정합니다.

      ```java
      // For Android
      // bool bFilter: 필터기능을 ON(true) / OFF(false)
      m_stylearView.setFilterEnabled(bool bFilter);
      ```

- **(3) 정지**  
    **StyleAR API `LIVE`** 동작을 정지합니다. StyleAR API에 설정되 UI 컴포넌트에 **결과 영상을 출력하는 것을 멈춥니다**. 

    ```java
    // For Android
    m_stylearView.stop();
    ```

### StyleAR API 적용방법(`STILL`)

**StyleAR API `STILL`** 을 적용하는 방법을 아래의 **Flow 다이어그램**의 순서대로 설명합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_Sample_Flow(STILL).png" width="150"></center><br/>

※ **StyleAR API `STILL`** 구동하기 위해서 우선 `(1)인스턴스 생성`을 하고 `(2)귀걸이를 설정`을 합니다.`(3) 구동 및 출력`을 통해 API가 동작하며 결과를 얻을 수 있습니다. 자세한 설명은 아래와 같습니다.

- **(1) 인스턴스 생성**  
    **StyleAR API`STILL`** 를 구동시키기위한 **API 인스턴스**를 **생성** 합니다.
  
  - 클래스 import  
    **StyleAR API`SITLL`** 을 구동하기 위한 클래스를 import 합니다.  

    ```java
    // StyleAR API STILL 컨트롤 클래스
    import xyz.deepixel.stylear.DPStyleAREarring;
    // StyleAR API 팩토리 클래스
    import xyz.deepixel.stylear.DPStyleARFactory;
    // StyleAR API 귀걸이 설정 클래스
    import xyz.deepixel.stylear.DPEarringParam;
    ```

  - 함수호출  
    **StyleAR API `STILL`** **인스턴스**를 **생성** 합니다. 만약 생성된 인스턴스가 있다면 해당 인스턴스를 리턴합니다.

    ```java
    //Context activity: StyleAR API가 포함된 activity를 입력
    DPStyleAREarring styleAR = DPStyleARFactory.getInstance(Context activity);
    ```

- **(2) 귀걸이 설정**  
    **StyleAR API `STILL`** 이 구동하기 이전에 호출해야 합니다. 귀걸이 설정방법은 **StyleAR API `LIVE`** 와 **동일**합니다.

    ```java
    // For Android
    // StyleAR API LIVE와 설정방법이 동일
    //    ...
    //    ...
    //    ...
    // StyleAR API STILL 에 좌측 귀걸이 정보 입력
    styleAR.setLeftEarringParam(earringParam);
    // StyleAR API STILL 에 우측 귀걸이 정보 입력
    styleAR.setLeftEarringParam(earringParam);
    ```

- **(3) 필터설정**  
    **StyleAR API `STILL`** 의 출력영상에 화면보정 필터의 ON/OFF 기능을 설정합니다.

    ```java
    // For Android
    // bool bFilter: 필터기능을 ON(true) / OFF(false)
    styleAR.setFilterEnabled(bool bFilter);
    ```

- **(4) 구동 및 출력**  
    **StyleAR API`STILL`** 에 사용자가 입력한 영상(**Bitmap 타입**), 좌측 및 우측 귀의 위치(대략적인)를 입력하여 구동하면 가상착용된 영상(**Bitmap 타입**)이 **입력한 귀의 위치**를 **기준**으로 출력됩니다.

    ```java
    // Bitmap bitmap: Bitmap 타입의 입력영상
    // Point left: 얼굴의 좌측 픽셀 위치(사용자 입력)
    // Point right: 얼굴의 우측 픽셀 위치(사용자 입력)
    // Bitmap processedBitmap: 귀걸이가 가상착용된 출력영상
    Bitmap processedBitmap = styleAR.getStyleAREarringImage(Bitmap bitmap, Point left, Point right);
    ```

- **(5) 메타데이터정보 획득**  
    **StyleAR API`STILL`** 이 구동된 이후에, 메타데이터를 획득할 수 있습니다. 메타데이터 출력 방법은 StyleAR API `LIVE` 와 동일합니다.

    ```java
    // For Android
    // 메타데이터 정보획득
    DPFaceMetaData faceMetaData = styleAR.getFaceMetaData();
    // 출력방법은 StyleAR API LIVE와 동일
    //  ...
    //  ...
    //  ...
    ```

***

## 연락처

StyleAR API 적용시 문제점을 발견하거나 궁금한 점이 있다면, 이메일(support@deepixel.xyz)로 문의해 주세요.  

***

## 참조

- [Android Sample code(LIVE)][android_sample_live]
- [Android Sample code(STILL)][android_sample_still]
- [Android 환경 설정][android_tutorial]
- [StyleAR API For Android][stylear_api_for_android]
- [StyleAR API 입력데이터 만들기][make_input_data]
- [딥픽셀 홈페이지][deepixel_hompage]

[android_sample_live]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/tutorial/android/StyleARForAndroidSample(LIVE)
[android_sample_still]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/tutorial/android/StyleARForAndroidSample(STILL)
[license]: /License/README.md
[android_tutorial]: /StyleAR/tutorial/android
[stylear_api_for_android]: /StyleAR/apis/android
[make_input_data]: /StyleAR/tutorial/input
[deepixel_hompage]: https://deepixel.azurewebsites.net/