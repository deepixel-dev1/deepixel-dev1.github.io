[![deepixel.xyz](./img/Deepixel_logo.PNG){: width="200"}](https://deepixel.azurewebsites.net/)

# StyleARRing Android 환경설정

**Android 플랫폼**에서 **StyleARRing API**를 적용하기 위한 라이브러리 등록 및 프로젝트 권한 설정에 대한 방법을 자세히 설명합니다.

## Android 프로젝트에서 StyleAR API 라이브러리 추가

> 본 예제는 stylearandroid-ringar-demosimple.aar 및 stylearring-ringar-demosimple.aar로 설정되어 있습니다. 실제 어플리케이션을 개발 할 시에는 **라이센스를 통해 받으신 라이브러리**로 설정하시기 바랍니다. **Android Studio 버전**은 **3.4.1** 입니다.

1. File->ProjectStructure를 클릭합니다.
2. 라이센스 발급된 라이브러리파일을 임포트 합니다.
    - Project Sturcture 다이얼로그의 +
    버튼을 클릭합니다.
   ![add library](./img/Addlibrary(1).png)
    - import AAR/JAR Package를 선택하고, next 를 클릭합니다. 
   ![add library](./img/Addlibrary(2).png)
    - 라이브러리 파일 선택을 위해 버튼을 클릭합니다.
   ![add library](./img/Addlibrary(3).png)
    - 저장된 배포 라이브러리 파일경로(1. stylearandroid, 2. stylearring)를 지정하고, OK 를 클릭합니다.
   ![add library](./img/Addlibrary(4).png)
    - 라이브러리를 apply 버튼을 통해 적용하고, 적용되었는지 확인합니다.
   ![add library](./img/Addlibrary(5).png)
    - 개발 어플리케이션의 dependencie에 추가한 라이브러리(2개)를 설정합니다.
   ![add library](./img/Addlibrary(6).png)

3. 어플리케이션 ID 설정
    > 적용할 프로젝트의 **어플리케이션 ID**는 **반드시 라이센스 발급 시 기입했던** '**적용 어플리케이션 ID**'를 사용해야 합니다.

    ```java
        android{
            defaultConfig {
            // 어플리케이션 ID = 라이센스 발급때 기입한 ID로 설정
            // 예시 라이센스 발급 ID(xyz.deepixel)
            applicationId "xyz.deepixel"
            }
        }
    ```

## Android 프로젝트에 권한설정 추가

> **StyleARRing API**를 개발 프로젝트의 어플리케이션에 적용하기 위해서는 **파일 입출력** 및 **카메라 사용**에 대한 **어플리케이션 사용자의 권한**이 **승인** 되어야 합니다.

- **사용권한 설정**  
    사용권한을 요청할 수 있게 manifests에 아래의 코드를 입력합니다.

    ```xml
    <!--For Android-->
    <!--manifests file-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
    ```

- **권한수락 코드(변경가능)**  
    설정된 권한이 모바일사용자에게 나타날 수 있도록 다이얼로그를 만들며, 선택에 따라 실제 권한이 적용 될 수 있도록 합니다. 해당 코드는 예시이며, **개발자가 다른 방식으로 기능구현**해도 무방합니다.

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
    // 에러 다이얼로그 생성
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> activity.finish())
                    .create();
        }

    }
    // 확인 다이얼로그 생성
    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                            (dialog, which) -> {
                                String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                if (permissions == null) {
                                    throw new IllegalArgumentException();
                                }
                                ActivityCompat.requestPermissions(getActivity(),
                                        permissions, args.getInt(ARG_REQUEST_CODE));
                            })
                    .setNegativeButton(android.R.string.cancel,
                            (dialog, which) ->
                                    ErrorDialog.newInstance(getString(args.getInt(ARG_NOT_GRANTED_MESSAGE)))
                                            .show(getChildFragmentManager(), FRAGMENT_DIALOG)).create();
        }

    }
    ```

***
