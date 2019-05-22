package xyz.deepixel.stylear_for_android_samplecode

import android.graphics.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private var mCameraFragment: CameraFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCameraFragment = CameraFragment.newInstance()
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}
