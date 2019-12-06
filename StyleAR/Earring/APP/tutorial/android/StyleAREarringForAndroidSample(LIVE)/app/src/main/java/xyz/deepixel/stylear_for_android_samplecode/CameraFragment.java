package xyz.deepixel.stylear_for_android_samplecode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import xyz.deepixel.stylear.earring.DPStyleAREarringView;
import xyz.deepixel.stylear.earring.DPEarringParam;
import xyz.deepixel.stylear.earring.DPFaceMetaData;


public class CameraFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    private DPStyleAREarringView m_stylearView;

    private EditText mEditTextMetaData;

    private Button m_enableFilterButton;

    private Button m_enableEarringFilterButton;

    private boolean m_filterEnabled = true;

    private boolean m_EarringfilterEnabled = true;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mEditTextMetaData = view.findViewById(R.id.matadata);

        m_stylearView = view.findViewById(R.id.stylear_view);
        m_enableFilterButton = view.findViewById(R.id.enable_image_filter_button);
        m_enableEarringFilterButton = view.findViewById(R.id.enable_earring_filter_button);

        m_stylearView.setOnTouchListener(new View.OnTouchListener() {
            final float MIN_DISTANCE = 10.0f;
            float x1 = 0.0f;
            float x2 = 0.0f;
            int imageIndex = 1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // Left to Right swipe action
                            if (x2 > x1) {
                                if (imageIndex <= 1)
                                    break;
                                imageIndex--;
                                m_stylearView.setEarringParam(getEarringParam(imageIndex));
                            } else if (x2 < x1) { // Right to left swipe action
                                if (imageIndex >= 2)
                                    break;
                                imageIndex++;
                                m_stylearView.setEarringParam(getEarringParam(imageIndex));
                            }
                        } else {
                            //메타데이터 출력
                            DPFaceMetaData faceMetaData = m_stylearView.getFaceMetaData();
                            StringBuilder msg = new StringBuilder();
                            msg.append("FRM : ").append(faceMetaData.getFaceRatioMean()).append('\n');
                            msg.append("FRS : ").append(faceMetaData.getFaceRatioStd()).append('\n');
                            msg.append("HCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getHairColorMean())).append('\n');
                            msg.append("HCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getHairColorStd())).append('\n');
                            msg.append("LCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getLipColorMean())).append('\n');
                            msg.append("LCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getLipColorStd())).append('\n');
                            msg.append("SCM : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getSkinColorMean())).append('\n');
                            msg.append("SCS : ").append(String.format("#%06X", 0xFFFFFF & faceMetaData.getSkinColorStd())).append('\n');

                            mEditTextMetaData.setText(msg.toString());
                            mEditTextMetaData.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return true;
            }
        });
        mEditTextMetaData.setVisibility(View.INVISIBLE);
        mEditTextMetaData.setOnTouchListener((view12, motionEvent) ->

        {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mEditTextMetaData.setVisibility(View.INVISIBLE);
            }
            return false;
        });

        m_enableFilterButton.setOnClickListener(v -> {
            // 이미지 필터 ON/OFF
            m_stylearView.setFilterEnabled(m_filterEnabled);
            m_filterEnabled = (m_filterEnabled == true) ? false : true;
        });

        m_enableEarringFilterButton.setOnClickListener(v -> {
            // 귀걸이 필터 ON/OFF
            m_stylearView.setEarringFilterEnabled(m_EarringfilterEnabled);
            m_EarringfilterEnabled = (m_EarringfilterEnabled == true) ? false : true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            // Set earring parameters.
            setEarringParam(1);

            // Start camera view.
            // StyleAR API LIVE 시작
            m_stylearView.start();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onPause() {
        // StyleAR API LIVE 종료
        m_stylearView.stop();
        super.onPause();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (requestCameraPermission()) return;
        if (requestReadExternalStoragePermission()) return;
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.camera_permission_not_granted))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.write_external_storage_permission_not_granted))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.intro_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }
        }
    }

    public void setEarringParam(int index) {
        // 귀걸이 설정
        DPEarringParam leftEarringParam = getEarringParam(index);
        DPEarringParam rightEarringParam = getFlipEarringParam(leftEarringParam);
        // 귀걸이 정보 StyleAR API LIVE에 입력(왼쪽)
        m_stylearView.setLeftEarringParam(leftEarringParam);
        // 귀걸이 정보 StyleAR API LIVE에 입력(오른쪽)
        m_stylearView.setRightEarringParam(rightEarringParam);
    }

    public DPEarringParam getFlipEarringParam(DPEarringParam earringParam) {
        DPEarringParam newEarringParam = (DPEarringParam) earringParam.clone();
        Bitmap source = earringParam.getBitmap();
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f, source.getWidth() / 2.0f, source.getHeight() / 2.0f);
        Bitmap flipBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        newEarringParam.setBitmap(flipBitmap);
        return newEarringParam;
    }

    public DPEarringParam getEarringParam(int index) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        DPEarringParam earringParam = new DPEarringParam();
        if (index == 1) {
            // 귀걸이 파일 리소스를 bitmap 타입으로 변경
            earringParam.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.earring0));
            // 귀걸이 실측 크기 설정
            earringParam.setSize(new SizeF(15.0f, 50.0f));
            // 귀걸이 핀 위치 설정
            earringParam.setAnchorPoint(new PointF(7.5f, 3.0f));
        } else if (index == 2) {
            // 귀걸이 파일 리소스를 bitmap 타입으로 변경
            earringParam.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.earring1));
            // 귀걸이 실측 크기 설정
            earringParam.setSize(new SizeF(40.0f, 50.0f));
            // 귀걸이 핀 위치 설정
            earringParam.setAnchorPoint(new PointF(20.0f, 3.0f));
        } else {
            throw new RuntimeException("Earring index is invalid.(" + index + ")");
        }
        return earringParam;
    }

    /**
     * Shows an error message dialog.
     */
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
                                            .show(getChildFragmentManager(), FRAGMENT_DIALOG))
                    .create();
        }

    }
}
