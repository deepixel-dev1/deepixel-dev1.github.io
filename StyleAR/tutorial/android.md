![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}

# StyleAR Android 환경설정

## Android Studio 환경설정

1. 프로젝트 생성한다.
2. File->ProjectStructure를 클릭한다.
3. 라이센스 발급된 라이브러리파일(StyleARAndroid.aar)을 임포트 한다.
   > 본 예제는 StyleARAndroid.aar이 아닌 stylearandroid-facear-demosimple.aar로 설정되어 있다.
    1. Project Sturcture 다이얼로그의 +
    버튼을 클릭한다.
   ![add library](./img/android_studio_add_library_1.png)
    1. import AAR/JAR Package를 선택하고, next 를 클릭한다. 
   ![add library](./img/android_studio_add_library_2.png)
    1. 라이브러리 파일 이름을 변경하기 위해 ... 버튼을 클릭한다.
   ![add library](./img/android_studio_add_library_3.png)
    1. 저장된 StyleARAndroid.aar파일경로를 지정하고, OK 를 클릭한다.
   ![add library](./img/android_studio_add_library_4.png)
4. 프로젝트 Gradle에 설정을 추가한다.

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
            implementation project(':StyleARAndroid')
        }
    ```

***
