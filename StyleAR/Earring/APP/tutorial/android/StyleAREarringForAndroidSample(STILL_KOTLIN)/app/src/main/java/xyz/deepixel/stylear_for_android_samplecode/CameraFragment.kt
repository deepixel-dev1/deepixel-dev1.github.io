package xyz.deepixel.stylear_for_android_samplecode

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
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
import android.widget.Toast

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.regex.Pattern

import xyz.deepixel.stylear.DPException
import xyz.deepixel.stylear.DPStyleAREarring
import xyz.deepixel.stylear.DPStyleARFactory
import xyz.deepixel.stylear.DPEarringParam

class CameraFragment : Fragment(), View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private var m_processImageButton: Button? = null

    private val mEditTextMetaData: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        m_processImageButton = view.findViewById(R.id.process_image_button)

        m_processImageButton!!.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val styleAR = DPStyleARFactory.getStyleAREarring(activity)

                // 영상 입력 및 Bitmap 타입으로 변경
                val inputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "test-image.png")
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath, options)

                // 귀걸이 설정
                val earringParam = getEarringParam(1)
                // 귀걸이 정보 StyleAR API에 입력(왼쪽)
                styleAR.setLeftEarringParam(earringParam)
                // 귀걸이 정보 StyleAR API에 입력(오른쪽)
                styleAR.setRightEarringParam(getFlipEarringParam(earringParam))

                // 화면보정 필터 설정
                styleAR.setFilterEnabled(false)

                // 귀걸이 보정 필터 설정
                styleAR.setEarringFilterEnabled(false)

                var processedBitmap: Bitmap
                try {
                    // StyleAR API STILL 구동 및 출력
                    processedBitmap = styleAR.getStyleAREarringImage(bitmap)
                } catch (e: DPException) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post { Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show() }
                    return
                }

                // 출력(결과) 영상 저장
                var outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "processed-image.jpg")
                var out: OutputStream? = null
                try {
                    out = FileOutputStream(outputFile)
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        out?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                // Get stylear image with earrings position.
                try {
                    // StyleAR API STILL 구동 및 출력 (얼굴 left, right 구분)
                    processedBitmap = styleAR.getStyleAREarringImage(bitmap, Point(100, 100), Point(200, 200))
                } catch (e: DPException) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post { Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show() }
                    return
                }

                // 출력(결과) 영상 저장
                outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "processed-image2.jpg")
                try {
                    out = FileOutputStream(outputFile)
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        out?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                // Show a success message.
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post { Toast.makeText(activity, "Saved a processed image.", Toast.LENGTH_SHORT).show() }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
        } else {
            requestPermission()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (requestReadExternalStoragePermission()) return
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
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
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
            val mEarringFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "155569.png")
            earringParam.bitmap = BitmapFactory.decodeFile(mEarringFile.absolutePath, options)
            earringParam.size = SizeF(7.5f, 34.0f)
            earringParam.anchorPoint = PointF(3.75f, 1.0f)
        } else if (index == 2) {
            val mEarringFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "155604.png")
            earringParam.bitmap = BitmapFactory.decodeFile(mEarringFile.absolutePath, options)
            earringParam.size = SizeF(4.0f, 58.0f)
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

        private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2
        private val FRAGMENT_DIALOG = "dialog"

        val RESULT_OK = -1

        /**
         * Tag for the [Log].
         */
        private val TAG = "Camera2BasicFragment"

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }
}
