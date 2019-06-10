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

import xyz.deepixel.stylear.ring.DPStyleARRingView;
import xyz.deepixel.stylear.ring.DPRingParam;
import xyz.deepixel.stylear.ring.DPHandMetaData;


public class CameraFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    private DPStyleARRingView m_stylearView;

    private EditText mEditTextMetaData;

    private Button m_enableFilterButton;

    private Button m_enableEarringFilterButton;

    private boolean m_filterEnabled = true;

    private boolean m_EarringfilterEnabled = true;

    private boolean[] m_bFingers = {false, false, true, true, false};

    private boolean m_isRight = false;

    private float m_ringOffset = 0.5f;

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

        m_stylearView = view.findViewById(R.id.stylear_ring_view);
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
                            } else if (x2 < x1) { // Right to left swipe action
                                if (imageIndex >= 3)
                                    break;
                                imageIndex++;
                            } else {
                                break;
                            }

                            setRingParam(imageIndex, 0);
                            setRingParam(imageIndex, 1);
                            setRingParam(imageIndex, 2);
                            setRingParam(imageIndex, 3);
                            setRingParam(imageIndex, 4);

                        } else {
                            //메타데이터 출력
                            DPHandMetaData handMetaData = m_stylearView.getHandMetaData();
                            StringBuilder msg = new StringBuilder();
                            msg.append("SCM : ").append(handMetaData.getSkinColorMean()).append('\n');
                            msg.append("FIN : ").append(handMetaData.getfinger2palmRatio() ).append('\n');


                            msg.append("FIN : ").append(handMetaData.getfinger2palmRatio() ).append('\n');
                            msg.append("NCM : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getNailColorMean())).append('\n');
                            msg.append("NCS : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getNailColorStd())).append('\n');
                            msg.append("SCM : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getSkinColorMean())).append('\n');
                            msg.append("SCS : ").append(String.format("#%06X", 0xFFFFFF & handMetaData.getSkinColorStd())).append('\n');
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            // Set earring parameters.
            setRingParam(1, 0);
            setRingParam(1, 1);
            setRingParam(1, 2);
            setRingParam(1, 3);
            setRingParam(1, 4);
            setProcessRingParam(m_bFingers, m_isRight, m_ringOffset);
            m_stylearView.setHandTemplateEnabled(true);
            setHandTemplate();

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

    public void setProcessRingParam(boolean[] fingers, boolean isRight, float offset){
        m_stylearView.setProcessRingParam(fingers, isRight, offset);
    }

    public void setRingParam(int ring_id, int nFingerID) {
        DPRingParam ringParam = getRingParam(ring_id);
        //DPRingParam rightEarringParam = getFlipEarringParam(leftEarringParam);
        m_stylearView.setRingParam(ringParam, nFingerID);

    }

    public void setHandTemplate(){
        File handTemplateFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "Hand_guide.png");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap handTemplate_bitmap = BitmapFactory.decodeFile(handTemplateFile.getAbsolutePath(), options);
        m_stylearView.setHandTemplate(handTemplate_bitmap);

    }

    public DPRingParam getRingParam(int index) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        DPRingParam ringParam = new DPRingParam();
        if (index == 1) {
            File mRingFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "ring_3.png");
            ringParam.setBitmap(BitmapFactory.decodeFile(mRingFile.getAbsolutePath(), options));
            //ringParam.setSize(new SizeF(30.0f, 10.0f));
            //ringParam.setAnchorPoint(new PointF(15.0f, 5.0f));
        } else if (index == 2) {
            File mRingFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "ring_3.png");
            ringParam.setBitmap(BitmapFactory.decodeFile(mRingFile.getAbsolutePath(), options));
            //ringParam.setSize(new SizeF(30.0f, 10.0f));
            //ringParam.setAnchorPoint(new PointF(15.0f, 5.0f));
        } else if (index == 3) {
            File mRingFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "ring_3.png");
            ringParam.setBitmap(BitmapFactory.decodeFile(mRingFile.getAbsolutePath(), options));
            //ringParam.setSize(new SizeF(30.0f, 10.0f));
            //ringParam.setAnchorPoint(new PointF(15.0f, 5.0f));
        } else if (index == 4) {
            File mRingFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "ring_3.png");
            ringParam.setBitmap(BitmapFactory.decodeFile(mRingFile.getAbsolutePath(), options));
            //ringParam.setSize(new SizeF(30.0f, 10.0f));
            //ringParam.setAnchorPoint(new PointF(15.0f, 5.0f));
        } else if (index == 0) {
            File mRingFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "ring_3.png");
            ringParam.setBitmap(BitmapFactory.decodeFile(mRingFile.getAbsolutePath(), options));
            //ringParam.setSize(new SizeF(30.0f, 10.0f));
            //ringParam.setAnchorPoint(new PointF(15.0f, 5.0f));
        } else {
            throw new RuntimeException("Ring index is invalid.(" + index + ")");
        }
        return ringParam;
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
