package xyz.deepixel.stylear_for_android_samplecode;

import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CameraFragment mCameraFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraFragment = CameraFragment.newInstance();
        if(null == savedInstanceState){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
