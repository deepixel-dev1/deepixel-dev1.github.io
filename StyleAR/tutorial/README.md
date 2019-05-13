[![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}](https://deepixel.azurewebsites.net/)

***

# StyleAR API Tutorial

StyleAR API는 모바일의 카메라 영상에서 특정 위치 검출, 3D 피팅, 영상분석 등을 통해 **사용자의 가상착용을 실시간으로 출력**하는 컴퓨터 비전 기반의 API Library입니다.<br/>  
StyleAR API에 대한 각각의 설명은 아래 목차의 **링크**를 따라가 주시기 바랍니다.

 [1. StyleAR API 기능](#stylear-api-기능)

 [2. StyleAR API 시스템 사양](#stylear-api-시스템-사양)

 [3. StyleAR API 사용방법](#stylear-api-사용방법)

 ***

## StyleAR API 기능

StyleAR API는 [귀걸이 착용](#귀걸이-착용), [화면보정필터](#화면보정필터), [얼굴 메타데이터](#얼굴-메타데이터) 기능을 제공하며 구동스팩은 아래와 같습니다.

|목차              |스팩                                |
|:-:               |:-:                                |
|**Platforms**     |Android                            |
|**Version**       |1.1.4                              |
|**SDK Size**      | ~ 30M                             |
|**허용 얼굴 각도** |yaw ≤ ±80º, pitch ≤ ±45º           |
|**가능인원**      |1명                                 |
|**속도**          | 20 ~ 40FPS @ 1080p (Garaxy Note 8)|
|**입력**          | 귀걸이 영상, 귀걸이 실측크기, 화면영상|
|**출력**          | 귀걸이 가상착용 영상, 메타데이터     |

### 귀걸이 착용

모바일 카메라 영상에서 사용자의 귀를 **실시간으로 검출**하고 양쪽 귀에 귀걸이가 착용된 영상을 제공합니다. 이것을 이용해서 사용자가 **귀걸이를 가상으로 착용**(Virtual Try-on)할 수 있는 서비스를 만들 수 있습니다.

<center> <img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAREarringResult.png" width="600"></center> <br/>

※ 귀걸이 착용 기능은 **반드시 귀걸이 영상과 귀걸이 실측 크기**를 입력해야 합니다. 귀걸이 영상을 만드는 방법은 [링크][make_input_data]를 참조해 주십시오.

### 화면보정필터

StyleAR API는 영상을 유려하게 보이기 위해, 자체적으로 출력영상에 **필터 알고리즘**을 적용하고 있습니다. 이 기능은 **ON / OFF** 가 가능합니다.

### 얼굴 메타데이터

StyleAR API는 영상에서 검출된 얼굴의 특징을 영상 분석하여 사람들 각각의 **고유정보**(메타데이터)를 출력할 수 있습니다. 이 기능의 사용 유무는 [라이센스 발급](#필수조건)에 따라 결정되며, 사용 시에도 **ON / OFF** 가 가능합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/meta_info.png" width="330"></center><br/>

※ 메타데이터는 얼굴 각 부분들의 **색상**(R<sup>Red</sup>G<sup>Green</sup>B<sup>Blue</sup>), **비율**, **좌표** 등의 특징을 분석하며 자세한 설명은 아래의 표와 같습니다.

|메타데이터|내용|단위|
|:-:|:-:|:-:|
|**피부 색상**|얼굴의 피부색을 나타낸다.|RGB순서로 색상정보가 출력<br>범위:0 ~ 255|
|**머리카락 색상**|머리카락 색을 나타낸다.|RGB순서로 색상정보가 출력<br>범위:0 ~ 255|
|**입술 색상**|입술 색을 나타낸다.|RGB순서로 색상정보가 출력 <br>범위:0 ~ 255|
|**왼쪽 귀 좌표**|오른쪽 귀의 좌표를 나타낸다.|스크린의 원점을 기준으로 x, y 좌표<br>범위: 스크린 크기 내|
|**오른쪽 귀 좌표**|왼쪽 귀의 좌표를 나타낸다.|스크린의 원점을 기준으로 x, y 좌표<br>범위: 스크린 크기 내|
|**얼굴 비율**| 얼굴의 가로 세로 비율을 나타낸다.|비율: 가로 / (가로 + 세로)<br>범위:0. ~ 1.<br> 가로: 코<->귀<br>세로: 코<->턱|

***

## StyleAR API 시스템 사양

StyleAR API는 **사용자의 편의**를 위해 **카메라 및 영상 컨트롤** 부분을 내부 라이브러리에 포함시켰습니다. 이에 개발자는 단순히 **StyleAR API만 호출하고 사용**함으로써 컨트롤 부분을 신경쓰지 않아도 됩니다.

- **StyleAR API 블록도**
  >StyleAR API와 Mobile Application 간의 관계를 나타낸 **블록도** 입니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_Block_Diagram.png" width="400"></center><br/>

- **StyleAR API의 state diagram**
  >StyleAR API의 기능들을 사용하기 위해서는 아래의 그림과 같이 간단한 state 메카니즘을 따라야 합니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_State_Diagram.png" width="600"></center><br/>

***

## StyleAR API 사용방법

이번 StyleAR API 사용방법에서는 StyleAR API를 사용하여 카메라 영상에서 사람얼굴 이 있을 시 귀의 위치에 귀걸이가 가상 착용되는 **모바일 어플리케이션**을 만들 것입니다. 예제코드는 [링크](https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/tutorial/android/StyleARForAndroidSample)를 참조해 주십시오.

### 필수조건

어플리케이션을 개발하기 앞서, StyleAR API를 사용하기 위해서는 **라이센스**를 발급받은 라이브러리 파일이 있어야 하며, 라이센스 된 라이브러리 파일을 개발 프로젝트에 사용하기 위한 **설정**을 해주어야 합니다.

- **라이센스 라이브러리 발급**  
  StyleAR API를 사용하기 위한 라이센스 라이브러리 발급은 아래의 표의 이메일로 문의바라며 내용은 **폼**에 맞게 작성해야 합니다.

|목차|내용|
|:-:|:-:|
|**발급**|<support@deepixel.xyz>로 문의 바랍니다.|
|**폼**|1.회사명<br> 2. 적용 어플리케이션 ID<br> 3. StyleAR API 사용기간<br> 4. 메타데이터기능 사용유무<br> 5. 담당자 연락처 |

- **환경설정**  
  환경설정에서는 StyleAR API 라이브러리를 프로젝트에 등록하는 방법을 설명 할 것입니다. 적용할 프로젝트 어플리케이션 ID는 **반드시 라이센스 발급 시 기입했던** '**적용 어플리케이션 ID**'를 사용하시기 바랍니다.
  - [Android 환경 설정][android_tutorial]

### StyleAR API 적용방법

StyleAR API를 **모바일에 적용**하기 위한 방법을 설명 합니다. 위의 **state diagram**을 보시고 함수 **호출 타이밍과 역할**을 생각하며 적용방법을 보면 서로에 대한 관계를 이해하기 쉽습니다. StyleAR API 적용방법 Flow는 아래의 그림과 같습니다.

<center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/StyleAR_Sample_Flow.png" width="500"></center><br/>

- **(1) StyleAR API 권한 설정**  
  StyleAR API를 개발 프로젝트 어플리케이션에 적용하기 위해서는 **파일 입출력** 및 **카메라 사용**에 대한 **어플리케이션 사용자의 권한**이 승인 되어야 합니다.

  - **사용권한 설정**  
    사용권한을 요청할 수 있게 프로젝트에 설정합니다.

    ```xml
    <!--For Android-->
    <!--manifests file-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
    ```

  - **권한수락 코드(개발자에 따른 변경가능)**  
    설정된 권한이 모바일사용자에게 나타날 수 있도록 다이얼로그를 만들며, 선택에 따라 실제 권한이 적용 될 수 있도록 합니다. 해당 코드는 예제이며, **개발자가 다른 방법을 사용하여 기능을 수행**해도 무방합니다.
 
    ```java
    /*For Android*/
    private val REQUEST_CAMERA_PERMISSION = 1
    private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2
    private val FRAGMENT_DIALOG = "dialog"
    // camera 권한 설정
    private boolean requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
            return false;
        else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION,
                    R.string.camera_permission_not_granted)
                    .show(getFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        return true;
    }
    // 파일입출력 권한 설정
    private boolean requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            return false;
        else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ConfirmationDialogFragment.newInstance(R.string.write_external_storage_permission_confirmation,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION,
                    R.string.write_external_storage_permission_not_granted)
                    .show(getFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
        return true;
    }
    ```

- **(2) StyleAR view UI생성 및 연결**  
  StyleAR API는 실시간으로 카메라로 부터 입력된 영상을 처리를 해야하는 라이브러리입니다. 하지만 **개발자의 카메라 설정에 따라 잘못된 결과를 출력** 할 수 있기 때문에, StyleAR API는 **내부에 카메라 및 viewer를 컨트롤** 하는 기능을 포함하고 있습니다. 따라서 StyleAR API를 사용을 할 시 StyleAR view UI를 만들고 StyleAR view controller를 연결하는 코드만 추가하면 **다른 설정 필요없이 사용**할 수 있습니다.

  - StyleAR view UI생성  
    Main view UI에 StyleAR view UI를 생성합니다.

    ```xml
    <!--For Android-->
    <xyz.deepixel.stylear.DPStyleARView
      android:id="@+id/stylear_view"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:layout_alignParentStart="true"
      android:layout_alignParentTop="true"
      android:layout_marginStart="0dp"
      android:layout_marginTop="0dp"
      android:background="#FFFFFFFF">
    </xyz.deepixel.stylear.DPStyleARView>
    ```

  - StyleAR view 연결  
    StyleAR view controller를 생성 한 StyleAR view UI에 연결합니다.

    ```java
    // For Android
    // StyleAR View Controller 선언
    private DPStyleARView m_stylearView
    // StyleAR View Controller를 layout의 view에 연결  
    m_stylearView = view.findViewById(R.id.stylear_view); //연결
    ```

- **(3) StyleAR API 구동**  
    StyleAR API동작을 시작합니다. StyleAR View Control에 연결된 **UI 컴포넌트**에 결과 영상을 출력합니다.

    ```java
    // For Android
    m_stylearView.start();
    ```

- **(4) StyleAR API 귀걸이 변경**  
    귀걸이 변경은 StyleAR API **구동 중간에도 변경**할 수 있으며 동시에 귀걸이가 변하는 것을 볼 수 있습니다. 귀걸이를 변경하기 위해서는 **귀걸이 영상**(bitmap)과 **귀걸이의 정보**(실제 귀걸이의 가로 크기(mm), 세로 크기(mm) 그리고 **핀 위치**[TOP or CENTER])가 필요합니다. 입력데이터를 만드는 방법은 [링크][make_input_data]를 참조하세요.

    <center><img src="https://deepixel-dev1.github.io/StyleAR/tutorial/img/earring_pin_position.png" width="120"></center>

    <center><b>귀걸이 핀위치</b></center><br>

    ```java
    // For Android
    // StyleAR API 귀걸이 정보 클래스 선언
    DPEarringParam earringParam = new DPEarringParam();
    // 1. 귀걸이 사진 파일 위치 (사진 파일(1) 또는 Bitmap(2) 형식으로 선택함.)
    earringParam.setAbsolutePath(mEarringFile.getAbsolutePath());
    // 2. 귀걸이 Bitmap 설정 (사진 파일(1) 또는 Bitmap(2) 형식으로 선택함.)
    BitmapFactory.Options options = new BitmapFactory.Options();
    // Bitmap 타입설정 (반듯이 RGB_8888)
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    // 귀걸이 Bitmap영상 설정
    earringParam.setBitmap(BitmapFactory.decodeFile(mEarringFile.getAbsolutePath(), options));
    // 실제 귀걸이 가로 크기(mm)
    earringParam.setWidth(13.0f);
    // 실제 귀걸이 세로 크기(mm)
    earringParam.setHeight(85.0f);
    // 귀걸이 핀 위치(TOP or CENTER)
    earringParam.setAnchorPosition(DPEarringAnchorPosition.TOP);
    // StyleAR API에 귀걸이 정보 클래스 및 귀걸이 파일 이름 입력
    m_stylearView.setEarringParam(earringParam);
    ```

- **(5) StyleAR API메타 정보 획득**  
    StyleAR API이 동작하는 동안 카메라 입력 영상에서의 **얼굴**에 대한 다양한 **메타 정보를 획득** 할 수 있습니다.

    ```java
    // For Android
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

- **(6) StyleAR API 정지**  
    StyleAR API 동작을 정지합니다. StyleAR API에 설정되 UI 컴포넌트에 **결과 영상을 출력하는 것을 멈춥니다**. 

    ```java
    // For Android
    m_stylearView.stop();
    ```

***

## 연락처

StyleAR API 적용시 문제점을 발견하거나 궁금한 점이 있다면, 이메일(support@deepixel.xyz)로 문의해 주세요.  

***

## 참조

- [Android Sample code][android_sample]
- [Android 환경 설정][android_tutorial]
- [StyleAR API For Android][stylear_api_for_android]
- [StyleAR API Input 만들기][make_input_data]

[android_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/tutorial/android/StyleARForAndroidSample
[license]: /License/README.md
[android_tutorial]: /StyleAR/tutorial/android
[stylear_api_for_android]: /StyleAR/apis/android
[make_input_data]: /StyleAR/tutorial/input