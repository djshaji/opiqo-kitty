package org.acoustixaudio.opiqo.opiqo.kitty;

import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
       System.loadLibrary("kitty");
//       System.loadLibrary("jalv");
    }

    boolean effectsEnabled = false;
    private String TAG = "[main]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Switch switch1 = findViewById(R.id.switch1);

        AudioEngine.create();
        AudioEngine.setDefaultStreamValues(this);
        String path = getApplicationInfo().nativeLibraryDir;
        AudioEngine.test(path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(TAG, "onCreate: " + path + " ->" + files.length);

        if (files != null) {
            for (File file : files) {
                // You can check if it's a file or a directory
                Log.d(TAG, "onCreate: " + file.getName());
            }
        } else {
            Log.d(TAG, "onCreate: null");
        }


        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {

            AudioEngine.setEffectOn(isChecked);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}