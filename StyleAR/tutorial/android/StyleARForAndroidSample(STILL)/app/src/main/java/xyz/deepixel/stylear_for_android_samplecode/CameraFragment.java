package xyz.deepixel.stylear_for_android_samplecode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import xyz.deepixel.stylear.DPException;
import xyz.deepixel.stylear.DPStyleAREarring;
import xyz.deepixel.stylear.DPStyleARFactory;
import xyz.deepixel.stylear.DPEarringParam;

public class CameraFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    public static final int RESULT_OK           = -1;

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    private Button m_processImageButton;

    private EditText mEditTextMetaData;

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

        m_processImageButton = view.findViewById(R.id.process_image_button);

        m_processImageButton.setOnClickListener(v -> {
            // 인스턴스 생성
            DPStyleAREarring styleAR = DPStyleARFactory.getStyleAREarring(getActivity());

            // 영상 입력 및 Bitmap 타입으로 변경
            File inputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "test-image.png");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);

            // 귀걸이 설정
            DPEarringParam earringParam = getEarringParam(1);
            styleAR.setLeftEarringParam(earringParam);
            styleAR.setRightEarringParam(getFlipEarringParam(earringParam));

            // 화면보정 필터 설정
            styleAR.setFilterEnabled(false);

            Bitmap processedBitmap;
            try {
                // StyleAR API STILL 구동 및 출력
                processedBitmap = styleAR.getStyleAREarringImage(bitmap);
            } catch (DPException e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }

            // 출력(결과) 영상 저장
            File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "processed-image.jpg");
            OutputStream out = null;
            try {
                out = new FileOutputStream(outputFile);
                processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Get stylear image with earrings position.
            try {
                // StyleAR API STILL 구동 및 출력 (얼굴 left, right 구분)
                processedBitmap = styleAR.getStyleAREarringImage(bitmap, new Point(100, 100), new Point(200, 200));
            } catch (DPException e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }

            // 출력(결과) 영상 저장
            outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "processed-image2.jpg");
            try {
                out = new FileOutputStream(outputFile);
                processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Show a success message.
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> Toast.makeText(getActivity(), "Saved a processed image.", Toast.LENGTH_SHORT).show());
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
        } else {
            requestPermission();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (requestReadExternalStoragePermission()) return;
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
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
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
            File mEarringFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "155569.png");
            earringParam.setBitmap(BitmapFactory.decodeFile(mEarringFile.getAbsolutePath(), options));
            earringParam.setSize(new SizeF(7.5f, 34.0f));
            earringParam.setAnchorPoint(new PointF(3.75f, 1.0f));
        } else if (index == 2) {
            File mEarringFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "155604.png");
            earringParam.setBitmap(BitmapFactory.decodeFile(mEarringFile.getAbsolutePath(), options));
            earringParam.setSize(new SizeF(4.0f, 58.0f));
            earringParam.setAnchorPoint(new PointF(2.0f, 1.0f));
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
