package xyz.deepixel.stylear_for_android_samplecode

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.os.Environment
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.SizeF
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import java.io.File

import xyz.deepixel.stylear.DPStyleAREarringView
import xyz.deepixel.stylear.DPEarringParam
import xyz.deepixel.stylear.DPFaceMetaData


class CameraFragment : Fragment(), View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private var m_stylearView: DPStyleAREarringView? = null

    private var mEditTextMetaData: EditText? = null

    private var m_enableFilterButton: Button? = null

    private var m_enableEarringFilterButton: Button? = null

    private var m_filterEnabled = true

    private var m_EarringfilterEnabled = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mEditTextMetaData = view.findViewById(R.id.matadata)

        m_stylearView = view.findViewById(R.id.stylear_view)
        m_enableFilterButton = view.findViewById(R.id.enable_image_filter_button)
        m_enableEarringFilterButton = view.findViewById(R.id.enable_earring_filter_button)

        m_stylearView!!.setOnTouchListener(object : View.OnTouchListener {
            internal val MIN_DISTANCE = 10.0f
            internal var x1 = 0.0f
            internal var x2 = 0.0f
            internal var imageIndex = 1

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> x1 = event.x
                    MotionEvent.ACTION_UP -> run{
                        x2 = event.x
                        val deltaX = x2 - x1

                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // Left to Right swipe action
                            if (x2 > x1) {
                                if (imageIndex <= 1)
                                    return@run
                                imageIndex--
                                m_stylearView!!.setEarringParam(getEarringParam(imageIndex))
                            } else if (x2 < x1) { // Right to left swipe action
                                if (imageIndex >= 2)
                                    return@run
                                imageIndex++
                                m_stylearView!!.setEarringParam(getEarringParam(imageIndex))
                            }
                        } else {
                            //메타데이터 출력
                            val faceMetaData = m_stylearView!!.faceMetaData
                            val msg = StringBuilder()
                            msg.append("FRM : ").append(faceMetaData.faceRatioMean).append('\n')
                            msg.append("FRS : ").append(faceMetaData.faceRatioStd).append('\n')
                            msg.append("HCM : ").append(String.format("#%06X", 0xFFFFFF and faceMetaData.hairColorMean)).append('\n')
                            msg.append("HCS : ").append(String.format("#%06X", 0xFFFFFF and faceMetaData.hairColorStd)).append('\n')
                            msg.append("LCM : ").append(String.format("#%06X", 0xFFFFFF and faceMetaData.lipColorMean)).append('\n')
                            msg.append("LCS : ").append(String.format("#%06X", 0xFFFFFF and faceMetaData.lipColorStd)).append('\n')
                            msg.append("SCM : ").append(String.format("#%06X", 0xFFFFFF and faceMetaData.skinColorMean)).append('\n')
                            msg.append("SCS : ").append(String.format("#%06X", 0xFFFFFF and faceMetaData.skinColorStd)).append('\n')

                            mEditTextMetaData!!.setText(msg.toString())
                            mEditTextMetaData!!.visibility = View.VISIBLE
                        }
                    }
                }
                return true
            }
        })
        mEditTextMetaData!!.visibility = View.INVISIBLE
        mEditTextMetaData!!.setOnTouchListener { view12, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                mEditTextMetaData!!.visibility = View.INVISIBLE
            }
            false
        }

        m_enableFilterButton!!.setOnClickListener { v ->
            // 이미지 필터 ON/OFF
            m_stylearView!!.setFilterEnabled(m_filterEnabled)
            m_filterEnabled = if (m_filterEnabled == true) false else true
        }

        m_enableEarringFilterButton!!.setOnClickListener { v ->
            // 귀걸이 필터 ON/OFF
            m_stylearView!!.setEarringFilterEnabled(m_EarringfilterEnabled)
            m_EarringfilterEnabled = if (m_EarringfilterEnabled == true) false else true
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            // Set earring parameters.
            setEarringParam(1)

            // Start camera view.
            // StyleAR API LIVE 시작
            m_stylearView!!.start()
        } else {
            requestPermission()
        }
    }

    override fun onPause() {
        // StyleAR API LIVE 종료
        m_stylearView!!.stop()
        super.onPause()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (requestCameraPermission()) return
        if (requestReadExternalStoragePermission()) return
    }

    private fun requestCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return false
        else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION,
                    R.string.camera_permission_not_granted)
                    .show(fragmentManager!!, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
        return true
    }

    private fun requestReadExternalStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return false
        else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ConfirmationDialogFragment.newInstance(R.string.write_external_storage_permission_confirmation,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION,
                    R.string.write_external_storage_permission_not_granted)
                    .show(fragmentManager!!, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.camera_permission_not_granted))
                        .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.write_external_storage_permission_not_granted))
                        .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.info -> {
                val activity = activity
                if (null != activity) {
                    AlertDialog.Builder(activity)
                            .setMessage(R.string.intro_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                }
            }
        }
    }

    fun setEarringParam(index: Int) {
        // 귀걸이 설정
        val leftEarringParam = getEarringParam(index)
        val rightEarringParam = getFlipEarringParam(leftEarringParam)
        // 귀걸이 정보 StyleAR API LIVE에 입력(왼쪽)
        m_stylearView!!.setLeftEarringParam(leftEarringParam)
        // 귀걸이 정보 StyleAR API LIVE에 입력(오른쪽)
        m_stylearView!!.setRightEarringParam(rightEarringParam)
    }

    fun getFlipEarringParam(earringParam: DPEarringParam): DPEarringParam {
        val newEarringParam = earringParam.clone() as DPEarringParam
        val source = earringParam.bitmap
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f, source.width / 2.0f, source.height / 2.0f)
        val flipBitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        newEarringParam.bitmap = flipBitmap
        return newEarringParam
    }

    fun getEarringParam(index: Int): DPEarringParam {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val earringParam = DPEarringParam()
        if (index == 1) {
            // 귀걸이 파일 로드
            val mEarringFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "155569.png")
            // 귀걸이 파일 bitmap 타입으로 변경
            earringParam.bitmap = BitmapFactory.decodeFile(mEarringFile.absolutePath, options)
            // 귀걸이 실측 크기 설정
            earringParam.size = SizeF(7.5f, 34.0f)
            // 귀걸이 핀 위치 설정
            earringParam.anchorPoint = PointF(3.75f, 1.0f)
        } else if (index == 2) {
            // 귀걸이 파일 로드
            val mEarringFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "155604.png")
            // 귀걸이 파일 bitmap 타입으로 변경
            earringParam.bitmap = BitmapFactory.decodeFile(mEarringFile.absolutePath, options)
            // 귀걸이 실측 크기 설정
            earringParam.size = SizeF(4.0f, 58.0f)
            // 귀걸이 핀 위치 설정
            earringParam.anchorPoint = PointF(2.0f, 1.0f)
        } else {
            throw RuntimeException("Earring index is invalid.($index)")
        }
        return earringParam
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity
            return AlertDialog.Builder(activity)
                    .setMessage(arguments!!.getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok) { dialogInterface, i -> activity!!.finish() }
                    .create()
        }

        companion object {

            private val ARG_MESSAGE = "message"

            fun newInstance(message: String): ErrorDialog {
                val dialog = ErrorDialog()
                val args = Bundle()
                args.putString(ARG_MESSAGE, message)
                dialog.arguments = args
                return dialog
            }
        }

    }


    class ConfirmationDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val args = arguments
            return android.support.v7.app.AlertDialog.Builder(activity!!)
                    .setMessage(args!!.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok
                    ) { dialog, which ->
                        val permissions = args.getStringArray(ARG_PERMISSIONS)
                                ?: throw IllegalArgumentException()
                        ActivityCompat.requestPermissions(activity!!,
                                permissions, args.getInt(ARG_REQUEST_CODE))
                    }
                    .setNegativeButton(android.R.string.cancel
                    ) { dialog, which ->
                        ErrorDialog.newInstance(getString(args.getInt(ARG_NOT_GRANTED_MESSAGE)))
                                .show(childFragmentManager, FRAGMENT_DIALOG)
                    }
                    .create()
        }

        companion object {

            private val ARG_MESSAGE = "message"
            private val ARG_PERMISSIONS = "permissions"
            private val ARG_REQUEST_CODE = "request_code"
            private val ARG_NOT_GRANTED_MESSAGE = "not_granted_message"

            fun newInstance(@StringRes message: Int,
                            permissions: Array<String>, requestCode: Int, @StringRes notGrantedMessage: Int): ConfirmationDialogFragment {
                val fragment = ConfirmationDialogFragment()
                val args = Bundle()
                args.putInt(ARG_MESSAGE, message)
                args.putStringArray(ARG_PERMISSIONS, permissions)
                args.putInt(ARG_REQUEST_CODE, requestCode)
                args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage)
                fragment.arguments = args
                return fragment
            }
        }

    }

    companion object {

        private val REQUEST_CAMERA_PERMISSION = 1
        private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2
        private val FRAGMENT_DIALOG = "dialog"

        /**
         * Tag for the [Log].
         */
        private val TAG = "Camera2BasicFragment"

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }
}
