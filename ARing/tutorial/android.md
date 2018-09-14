
# ARing API 안드로이드 환경설정

## ARing API 파일 설정

> ARing API 및 3rd Party의 파일(library, include)위치 설정

```text
1. OpenCV  
  ▶ include  
  /해당프로젝트/src/main/cpp/include/opencv_3.3.1/..
  ▶ library  
  /해당프로젝트/src/main/cpp/libs/armeabi-v7a/libopencv_world.so
2. TBB
  ▶ library  
  /해당프로젝트/src/main/cpp/include/tbb/libtbb.so
3. ARing API
  ▶ include  
  /해당프로젝트/src/main/cpp/include/aringnative/DPEception.h
  /해당프로젝트/src/main/cpp/include/aringnative/DPFactoryForAndroid.h
  /해당프로젝트/src/main/cpp/include/aringnative/IARing.h
  ▶ library  
  /해당프로젝트/src/main/cpp/libs/armeabi-v7a/libaringnative-lib.so 
```

## Native C++ 연결을 위한 설정

1. build.gradle

    ```gradle
    externalNativeBuild {
                cmake {
                    cppFlags "-std=c++11 -frtti -fexceptions -fopenmp"
                }
                ndk {
                    // Specifies the ABI configurations of your native
                    // libraries Gradle should build and package with your APK.
                    abiFilters 'armeabi-v7a'
                }
            }
    ```

2. CMakeList

   * include 디렉토리 설정
     ```cmake
     include_directories(src/main/cpp/include/opencv_3.3.1/
       src/main/cpp/include/aringnative/ src/main/cpp/include/)
     ```
   * library 디렉토리 설정
     ```cmake
     set(LIB_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/libs/)
     ```
   * 3rd Party(OpenCV, TBB) library 추가
     ```cmake
     add_library( opencv-lib
                  SHARED
                  IMPORTED )

     set_target_properties( # Specifies the target library.
                            opencv-lib
                            # Specifies the parameter you want to define.
                            PROPERTIES IMPORTED_LOCATION
                            # Provides the path to the library you want to import.
                            ${LIB_DIR}${ANDROID_ABI}/libopencv_world.so )

     add_library( tbb-lib
                  SHARED
                  IMPORTED )

     set_target_properties( # Specifies the target library.
                            tbb-lib
                            # Specifies the parameter you want to define.
                            PROPERTIES IMPORTED_LOCATION
                            # Provides the path to the library you want to import.
                            ${LIB_DIR}${ANDROID_ABI}/libtbb.so )
     ```
   * ARing Library 추가
     ```cmake
     add_library( aring-lib
                  SHARED
                  IMPORTED)

     set_target_properties( # Specifies the target library.
                            aring-lib
                            # Specifies the parameter you want to define.
                            PROPERTIES IMPORTED_LOCATION
                            # Provides the path to the library you want to import.
                            ${LIB_DIR}${ANDROID_ABI}/libaringnative-lib.so )
     ```

   * 사용자 파일 library(native-lib.cpp) 추가
     ```cmake
     add_library( # Sets the name of the library.
                  native-lib
                  # Sets the library as a shared library.
                  SHARED
                  # Provides a relative path to your source file(s).
                  src/main/cpp/src/native-lib.cpp )
     ```
   * library 링크
     ```cmake
     target_link_libraries( # Specifies the target library.
                            native-lib

                            opencv-lib
                            tbb-lib
                            aring-lib
                            # Links the target library to the log library
                            # included in the NDK.
                            ${log-lib} )
     ```
*****