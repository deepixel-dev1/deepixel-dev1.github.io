package maka.deepixel.xyz.makaandroidappexample;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;

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
        init(this, "com.vaultmicro.camerafi.live_maka_deepixel_license.bytes");

        // For testing
        // You have to insert "test_video.avi" video into /Android/data/your_applicationId/files directory.
        File f = getExternalFilesDir(null);
        String videoAbolutePath = f.getAbsolutePath() + File.separator + String.format("test_video.avi");
        process(videoAbolutePath);
        TextView textView = (TextView)findViewById(R.id.sample_text);
        textView.setText("Processing is over.");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void init(Activity activity, String licenseFilename);

    public native void process(String videoAbsolutePath);
}
