[MAKA]: https://deepixel-dev1.github.io/makanative/tutorial/

# LICENSE TUTORIAL (For Android)

>A license is required to use the software library provided by Deepixel Inc.. The license is included in library file.

## Request a license

Please send us the following information required to generate and manage the license file by [deepixel@deepixel.xyz]

- Application ID
  >Application ID to which the license applies.  

- Your company name
- E-mail
  >E-mail address for an important notice.  

## Apply a license

1. Make sure that the application id should be same as the one you provided when you requested the license.

2. Pass the main activity object to the JNI function.

   ```java  
   package maka.deepixel.xyz.makaandroidappexample;

   import android.app.Activity;
   import android.support.v7.app.AppCompatActivity;
   import android.os.Bundle;

   public class MainActivity extends AppCompatActivity {

       // Used to load the 'native-lib' library on application startup.
       static {
           System.loadLibrary("native-lib");
       }

       @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main);

           // Example of a call to a native method
           init(this);
       }

       /**
        * A native method that is implemented by the 'native-lib' native library,
        * which is packaged with this application.
        */
       public native void init(Activity activity);
   }

   ```

3. Create an instance with the main activity as arguments.

    The following is an example for [MAKA] API.

    ```cpp
    #include <jni.h>
    #include <android/log.h>
    #include <DPFactoryForAndroid.h>
    #include <IMaka.h>
    #include <DPException.h>

    #define LOGF(tag, ...) __android_log_print(ANDROID_LOG_FATAL  , tag, __VA_ARGS__)

    extern "C" {

    std::shared_ptr<dp::makanative::IMaka> g_ptrMaka;

    JNIEXPORT void JNICALL
    Java_maka_deepixel_xyz_makaandroidappexample_MainActivity_init(JNIEnv *env, jobject,
           jobject activity, jstring licenseFilename) {
        try {
            g_ptrMaka = dp::android::DPFactoryForAndroid::CreateInstance<dp::makanative::IMaka>
                    (env, activity);
        } catch (dp::exception::DPLicenseExpiredException ex) {
            LOGF("MAKA", "%s", ex.what());
        }
    }

    }
    ```

## Exceptions

- dp::exception::DPLicenseException
  
  >This exception is occurred when the license file does not exist or is corrupted.

- dp::exception::DPLicenseExpiredException

  >This exception is occurred when the license file is expired.
