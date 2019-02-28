![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}

# StyleAR API Tutorial

>StyleAR API는 모바일의 카메라 영상에서 얼굴의 특정 위치 검출, 3D 피팅, 영상분석 등을 통해 귀걸이 착용 영상과 메타정보를 실시간으로 출력하는 컴퓨터 비전 기반의 API Library입니다.  

![aring display](./img/face(earring).png){: width="200"}

***

## 특징

### 귀걸이 착용

> 모바일의 카메라 영상에서 사용자의 귀를 실시간으로 검출하여 양쪽 귀에 귀걸이가 착용된 영상을 제공합니다. 이것을 이용해서 사용자가 귀걸이를 가상으로 착용(Virtual Try-on)할 수 있는 서비스를 만들 수 있습니다.

- 스팩:

    |목차|스팩|비고|
    |:-:|:-:|:-:|
    |가능인원|1명||
    |속도| 20 ~ 40FPS (Frame Per Secound) |실험 장비(Galaxy Note 8), 해상도(1440 X 1080)|

- 입력 및 출력:

    |목차|입력|비고||목차|출력|비고|
    |:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
    |얼굴영상|![styleAR result](./img/faceInput.png){: width="700"}|얼굴이 사진의 40%이상 차지 해야 함||결과영상|![styleAR result](./img/faceOutput.png){: width="700"}|귀걸이가 실측크기에 맞게 배치 됨|
    |귀걸이 사진|![styleAR result](./img/input_1.png){: width="500"}|● 영상편집 시 귀걸이 위와 아래의 공백을 최대한 작게<br />● 영상은 귀걸이를 제외한 배경은 투명처리(png 포멧)||메타데이터|![deepixel.xyz](./img/meta_info.png){: width="700"}|● 얼굴비율[가로: 코에서 턱, 세로: 코에서 귀]: 가로 /(가로 + 세로) {범위:0 ~ 1}<br />● 피부, 입술, 머리카락 색상: RGB순서로 색상정보가 출력 {범위:0 ~ 255}<br />● 귀 위치: 스크린의 원점을 기준으로 x, y 좌표|
    |귀걸이 크기|귀걸이 실측 가로와 세로 크기|단위(mm)|||||

***

## StyleAR API

- StyleAR API의 구조

  ![deepixel.xyz](./img/structure.png){: width="800"}

- StyleAR API의 life cycle

  ![lifecycle](./img/life_cycle.png){: width="500"}

## StyleAR API 사용방법

>StyleAR API를 모바일 앱에서 사용하는 방법을 설명합니다.

- 라이센스 발급
  > StyleAR API를 사용하기 위해서는 반드시 라이센스를 발급받아야 합니다.

|목차|내용|
|:-:|:-:|
|발급|<support@deepixel.xyz>로 문의 바랍니다.|
|주의사항|API 사용 기간과 메타정보 사용 유무를 고려하여 주시기 바랍니다.|

- 환경설정
  - Android
    - StyleAR API Library 파일 넣기
    > Library 폴더에 라이센스 발급 시 배포된 라이브러리 파일(StyleARAndroid.aar)을 넣는다.
    - Gradle 설정추가

        ```Gradle
        android{
          compileOption{
          sourceCompatibility 1.8
          sourceCompatibility 1.8
          }
        }

        dependencies{
            implementation 'net.sourceforge.streamsupport:android-retrofuture:1.7.0'
            implementation 'com.android.support:appcompat-v7:28.0.0'
            implementation 'com.android.support.constraint:constraint-layout:1.1.3'
        }
        ```

  - iOS
    - [iOS 환경 설정][ios_tutorial]

- StyleAR API 사용법
  > [Android][android_sample] 예제는 [Camera2BasicFragment][camera2basicfragment_sample] 예제 코드를 기반으로 구현하였습니다.

  - StyleAR API 객체 생성 및 초기화
    > DPStyleARFactory를 사용하여 StyleAR API객체를 생성합니다. 객체를 초기화하는 과정에서 라이센스와 관련된 예외가 발생할 수 있습니다.

    ```java
    // For Android
    if (mStyleARAndroid == null) {
        try {
            // StyleAR API 객체생성
            mStyleARAndroid = DPStyleARFactory.getInstance(this.getActivity());
            mStyleARAndroid.initialize();
            // 라이센스 오류 메시지 출력
        } catch (DPLicenseExpiredException e) {
            ErrorDialog.newInstance(e.getMessage()).show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } catch (DPLicenseException e) {
            ErrorDialog.newInstance(e.getMessage()).show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } catch (DPException e) {
            ErrorDialog.newInstance(e.getMessage()).show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }
    ```

    ```swift
    // For iOS
    // StyleAR API객체를 생성하고 초기화한다.
    id<DPStyleAR> styleAR = [DPStyleARFactory getInstance];
    @try {
        [styleAR initialize];
    }
    @catch(DPLicenseExpiredException *e) {
        std::cout << [e reason] << std::endl;
    }
    @catch(DPLicenseException *e) {
        std::cout << [e reason] << std::endl;
    }
    @catch(DPException *e) {
        std::cout << [e reason] << std::endl;
    }
    ```

  - StyleAR API 설정
    > StyleAR API를 초기화하고 start 함수를 호출하기 전에 카메라 정보와 StyleAR의 결과가 출력될 UI 컴포넌트를 설정해야 합니다.

    ```java
    // For Android
    // DPCameraParam 선언
    DPCameraParam cameraParam = new DPCameraParam();
    // SensorOrientation 설정
    cameraParam.setSensorOrientation(mSensorOrientation);
    // focallength 설정
    cameraParam.setFocalLength(m_focalLength);
    // ui surface 설정
    mStyleARAndroid.setTargetSurface(mSurface);
    // StyleAR API에 카메라 파라메터(DPCameraParam) 설정
    mStyleARAndroid.setCameraParam(cameraParam);
    ```

    ```swift
    // For iOS
    // 카메라 정보를 설정한다.
    DPCameraParam *cameraParam = [[DPCameraParam alloc] init];
    [cameraParam setSensorOrientation:90];
    [cameraParam setFocalLength:30.0f];
    [_styleAR setCameraParam:cameraParam];

    // StyleAR API에 view를 설정한다.
    // 센서 방향대로 영상을 회전한다.
    CGAffineTransform rotation = CGAffineTransformMakeRotation(M_PI / 2);
    // 전면 카메라가 거울처럼 보이도록 좌우를 뒤집는다.
    CGAffineTransform mirror = CGAffineTransformMakeScale(-1.0, 1.0);
    targetView.layer.affineTransform = CGAffineTransformConcat(rotation, mirror);
    targetView.layer.contentsGravity = kCAGravityResizeAspect;
    targetView.layer.frame = targetView.bounds;
    [_styleAR setTargetView:targetView];
    ```

    - StyleAR API 카메라 입력 설정
    > 카메라 영상을 프로세싱하기 위해 이벤트 핸들러를 설정해야 합니다. 이벤트 핸들러는 StyleAR API 객체에서 가져올 수 있습니다. 이 후에 카메라와 StyleAR API를 구동하면 카메라 영상이 프로세싱됩니다. 카메라 영상은 4:3 비율로 설정하는 것을 권장합니다. 일반적으로 모바일의 카메라 영상은 4:3 비율이 16:9 비율보다 FOV(Field Of View)가 넓기 때문에 더 좋은 사용자 경험(User Experience)을 제공합니다.

    ```java
    // For Android
    // ImageReader 생성
    mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),ImageFormat.YUV_420_888, /*maxImages*/5);
    // StyleAR API 이벤트 핸들러 설정
    mImageReader.setOnImageAvailableListener(mStyleARAndroid.getOnImageAvailableListener(), mBackgroundHandler);
    ```

    ```swift
    // For iOS
    // 캡쳐 세션을 생성하고 초기화한다.
    AVCaptureSession *captureSession = [[AVCaptureSession alloc] init];
    captureSession.sessionPreset = AVCaptureSessionPresetPhoto;

    // 캡쳐 세션의 input을 설정한다.
    AVCaptureDevice *device = [self frontCamera];
    NSError *error = nil;
    AVCaptureDeviceInput *input = [[AVCaptureDeviceInput alloc] initWithDevice:device error:&error];
    [captureSession addInput:input];

    // 갭쳐 세션의 output을 설정한다.
    AVCaptureVideoDataOutput *output = [[AVCaptureVideoDataOutput alloc] init];
    output.videoSettings = @{(NSString *)kCVPixelBufferPixelFormatTypeKey : @(kCVPixelFormatType_32BGRA)};
    output.alwaysDiscardsLateVideoFrames = YES;
    [captureSession addOutput:output];

    // 카메라 영상을 처리하는 대리자를 지정한다.
    id<AVCaptureVideoDataOutputSampleBufferDelegate> dele = [_styleAR getDelegate];
    dispatch_queue_t queue = dispatch_queue_create("VideoQueue", DISPATCH_QUEUE_SERIAL);
    [output setSampleBufferDelegate:dele queue:queue];


    // 정면 카메라를 반환하는 함수
    - (AVCaptureDevice *)frontCamera {
        NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
        for (AVCaptureDevice *device in devices) {
            if ([device position] == AVCaptureDevicePositionFront) {
                return device;
            }
        }
        return nil;
    }
    ```

    - StyleAR API 귀걸이 변경
    > 귀걸이를 변경하기 위해서는 귀걸이 사진의 절대 경로와 귀걸이의 정보(실제 귀걸이의 가로 크기(mm), 세로 크기(mm) 그리고 핀 위치[TOP or CENTER])가 필요합니다.

    ![earring pin position](./img/earring_pin_position.png){: width="250"}

    ```java
    // For Android
    // StyleAR API 귀걸이 정보 클래스 선언
    DPEarringParam earringParam = new DPEarringParam();
    // 귀걸이 사진 파일 위치
    earringParam.setAbsolutePath(mEarringFile.getAbsolutePath());
    // 실제 귀걸이 가로 크기(mm)
    earringParam.setWidth(13.0f);
    // 실제 귀걸이 세로 크기(mm)
    earringParam.setHeight(85.0f);
    // 귀걸이 핀 위치(TOP or CENTER)
    earringParam.setAnchorPosition(DPEarringAnchorPosition.TOP);
    // StyleAR API에 귀걸이 정보 클래스 및 귀걸이 파일 이름 입력
    mStyleARAndroid.setEarringParam(earringParam);
    ```

    ```swift
    // For iOS
    // 귀걸이 정보를 설정한다.
    NSString *earringPath = GetEarringImgPath();
    DPEarringParam *earringParam = [[DPEarringParam alloc] init];
    earringParam.absolutePath = earringPath;
    earringParam.width = 13.0f;
    earringParam.height = 85.0f;
    earringParam.anchorPosition = TOP;
    [_styleAR setEarringParam:earringParam];
    ```

    - StyleAR API 구동
    > StyleAR API동작을 시작합니다. StyleAR API에 설정된 UI 컴포넌트에 결과 영상을 출력합니다. StyleAR API을 구동하기 전에 카메라 관련 정보와 UI 컴포넌트를 설정해야 하고, 카메라 영상을 프로세싱하기 위한 이벤트 핸들러를 등록해야 합니다.

    ```java
    // For Android
    mStyleARAndroid.start();
    ```

    ```swift
    // For iOS
    [_styleAR start];
    ```

    - StyleAR API 정지
    > StyleAR API 동작을 정지합니다. StyleAR API에 설정되 UI 컴포넌트에 결과 영상을 출력하는 것을 멈춥니다. 카메라 관련 정보, UI 컴포넌트 또는 이벤트 핸들러를 다시 등록하는 것을 StyleAR API동작을 정지한 후에 가능합니다.

    ```java
    // For Android
    mStyleARAndroid.stop();
    ```

    ```swift
    // For iOS
    [_styleAR stop];
    ```

    - StyleAR API메타 정보 획득
    > StyleAR API이 동작하는 동안 카메라 입력 영상에서 다양한 메타 정보를 획득 할 수 있습니다.

    ```java
    // For Android
    DPFaceMetaData faceMetaData = mStyleARAndroid.getFaceMetaData();
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

    ```swift
    // For iOS
    DPFaceMetaData *faceMetaData = [styleAR getFaceMetaData];
    NSMutableString *str = [[NSMutableString alloc] init];
    [str appendFormat:@"FRM = %f\n", faceMetaData.faceRatioMean];
    [str appendFormat:@"FRS = %f\n", faceMetaData.faceRatioStd];
    [str appendFormat:@"HCM = #%06X\n", 0xFFFFFF & faceMetaData.hairColorMean];
    [str appendFormat:@"HCS = #%06X\n", 0xFFFFFF & faceMetaData.hairColorStd];
    [str appendFormat:@"LCM = #%06X\n", 0xFFFFFF & faceMetaData.lipColorMean];
    [str appendFormat:@"LCS = #%06X\n", 0xFFFFFF & faceMetaData.lipColorStd];
    [str appendFormat:@"SCM = #%06X\n", 0xFFFFFF & faceMetaData.skinColorMean];
    [str appendFormat:@"SCS = #%06X", 0xFFFFFF & faceMetaData.skinColorStd];
    ```

## 연락처

> StyleAR API 적용시 문제점을 발견하거나 궁금한 점이 있다면, 이메일(support@deepixel.xyz)로 문의해 주세요.  

***

## 참조

- [Android Sample code][android_sample]
- [camera2basicfragment][camera2basicfragment_sample]
- [iOS Sample code][ios_sample]
- [iOS 환경 설정][ios_tutorial]
- [StyleAR API For Android][stylear_api_for_android]
- [StyleAR API For iOS][stylear_api_for_ios]

[android_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/tutorial/android/StyleARForAndroidSample
[license]: /License/README.md
[camera2basicfragment_sample]: https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/com/example/android/camera2basic/Camera2BasicFragment.java
[ios_sample]: https://github.com/deepixel-dev1/deepixel-dev1.github.io/tree/master/StyleAR/tutorial/ios/StyleARiOSAppExample
[ios_tutorial]: /StyleAR/tutorial/ios
[stylear_api_for_android]: /StyleAR/apis/android
[stylear_api_for_ios]: /StyleAR/apis/ios
