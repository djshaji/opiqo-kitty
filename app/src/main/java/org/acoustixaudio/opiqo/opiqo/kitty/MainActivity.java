package org.acoustixaudio.opiqo.opiqo.kitty;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.slider.Slider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
       System.loadLibrary("kitty");
//       System.loadLibrary("jalv");
    }

    boolean effectsEnabled = false;
    private String TAG = "[main]";
    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        switch1 = findViewById(R.id.switch1);

        AudioEngine.create();
        AudioEngine.setDefaultStreamValues(this);
//        String path = unzip();
        String path = new StringBuilder ()
                .append(getFilesDir()).append("/lv2").toString();
        Log.d(TAG, "onCreate: [lv2 path] " + path);
        copyAssetsToFiles("lv2");

        AudioEngine.test(path);
        File directory = new File(path);
        listFilesRecursive(directory);
        File[] files = directory.listFiles();
        Log.d(TAG, "onCreate: [lv2 path] " + path + " ->" + files.length);

//        String filename = "/data/user/0/org.acoustixaudio.opiqo.opiqo.kitty/files/lv2/gx_sloopyblue.lv2/manifest.ttl";
//        try {
//            Log.d(TAG, "onCreate: [test ex]" + readFileToString(new File(filename)));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
                requestAudioPermission();
            else
                AudioEngine.setEffectOn(isChecked);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Slider slider = findViewById(R.id.slider1);
        slider.addOnChangeListener((s, value, fromUser) -> {
            if (fromUser) {
                AudioEngine.setValue(1, value / 100f);
            }
        });
        Slider slider2 = findViewById(R.id.slider2);
        slider2.addOnChangeListener((s, value, fromUser) -> {
            if (fromUser) {
                AudioEngine.setValue(2, value / 100f);
            }
        });
        Slider slider3 = findViewById(R.id.slider3);
        slider3.addOnChangeListener((s, value, fromUser) -> {
            if (fromUser) {
                AudioEngine.setValue(3, value / 100f);
            }
        });

        TextView about = findViewById(R.id.meow);
        about.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, About.class));
        });
    }

    public String unzipLV2() {
        File dir = new File(getFilesDir(), "lv2");
        if (!dir.exists()) dir.mkdirs();
        Log.d(TAG, "unzipLV2: " + dir.getAbsolutePath());

        android.content.res.AssetManager am = getAssets();
        try {
            extractAssetRecursive(am, "lv2/sloopy.zip", dir);
        } catch (java.io.IOException e) {
            Log.e(TAG, "unzipLV2 failed", e);
        }

        return dir.getAbsolutePath();
    }

    public String unzip() {
        File dir = new File(getFilesDir(), "lv2");
        if (!dir.exists()) dir.mkdirs();
        Log.d(TAG, "unzipLV2: " + dir.getAbsolutePath());

        android.content.res.AssetManager am = getAssets();
        try (java.io.InputStream is = am.open("lv2/sloopy.zip");
             java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(is)) {

            java.util.zip.ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(dir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();
                    try (java.io.OutputStream os = new java.io.FileOutputStream(outFile)) {
                        int read;
                        while ((read = zis.read(buffer)) != -1) {
                            os.write(buffer, 0, read);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (java.io.IOException e) {
            Log.e(TAG, "unzipLV2 failed", e);
        }

        return dir.getAbsolutePath();
    }

    private void extractAssetRecursive(android.content.res.AssetManager am, String assetPath, File outDir) throws java.io.IOException {
        String[] list = am.list(assetPath);
        if (list == null || list.length == 0) {
            // It's a file; copy it
            java.io.File outFile = new File(outDir, new File(assetPath).getName());
            try (java.io.InputStream in = am.open(assetPath);
                 java.io.OutputStream out = new java.io.FileOutputStream(outFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } else {
            // It's a directory; create corresponding directory (skip creating extra top-level "lv2" folder)
            File targetDir = outDir;
            if (!"lv2".equals(assetPath)) {
                targetDir = new File(outDir, new File(assetPath).getName());
                targetDir.mkdirs();
            }
            for (String name : list) {
                String childPath = assetPath + "/" + name;
                extractAssetRecursive(am, childPath, targetDir);
            }
        }
    }

    private void listAllAssets() {
        try {
            android.content.res.AssetManager am = getAssets();
            List<String> assetFiles = new ArrayList<>();
            listAssetRecursive(am, "", assetFiles);
            Log.d(TAG, "listAllAssets: total=" + assetFiles.size());
            for (String a : assetFiles) {
                Log.d(TAG, "asset: " + a);
            }
        } catch (java.io.IOException e) {
            Log.e(TAG, "listAllAssets failed", e);
        }
    }

    private void listAssetRecursive(android.content.res.AssetManager am, String path, List<String> out) throws java.io.IOException {
        String[] list = am.list(path);
        if (list == null || list.length == 0) {
            if (!path.isEmpty()) {
                out.add(path);
            }
        } else {
            for (String name : list) {
                String child = path.isEmpty() ? name : path + "/" + name;
                listAssetRecursive(am, child, out);
            }
        }
    }

    private void listFilesRecursive(File dir) {
        if (dir == null || !dir.exists()) return;
        File[] entries = dir.listFiles();
        if (entries == null) return;
        for (File f : entries) {
            if (f.isDirectory()) {
                listFilesRecursive(f);
            } else {
                Log.d(TAG, "listFilesRecursive: " + dir.getAbsolutePath() + ":" + f.getAbsolutePath());
            }
        }
    }

    private String copyAssetsToFiles(String assetDir) {
        File baseDir = getFilesDir();
        try {
            copyAssetDir(getAssets(), assetDir, baseDir);
        } catch (java.io.IOException e) {
            Log.e(TAG, "copyAssetsToFiles failed", e);
        }

        return baseDir.getAbsolutePath();
    }

    private void copyAssetDir(android.content.res.AssetManager am, String assetPath, File outDir) throws java.io.IOException {
        String[] list = am.list(assetPath);
        if (list == null || list.length == 0) {
            // It's a file
            String name = assetPath.contains("/") ? assetPath.substring(assetPath.lastIndexOf('/') + 1) : assetPath;
            File outFile = new File(outDir, name);
            try (java.io.InputStream in = am.open(assetPath);
                 java.io.OutputStream out = new java.io.FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = in.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }
            }
        } else {
            // It's a directory
            File targetDir = outDir;
            if (!assetPath.isEmpty()) {
                String name = assetPath.contains("/") ? assetPath.substring(assetPath.lastIndexOf('/') + 1) : assetPath;
                targetDir = new File(outDir, name);
                if (!targetDir.exists()) targetDir.mkdirs();
            }
            for (String name : list) {
                String child = assetPath.isEmpty() ? name : assetPath + "/" + name;
                copyAssetDir(am, child, targetDir);
            }
        }
    }

    public static String readFileToString(File file) throws IOException {
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) out.write(buf, 0, r);
            return out.toString("UTF-8");
        }
    }



    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Audio permission granted");
                if (switch1.isChecked()) {
                    AudioEngine.setEffectOn(true);
                }

                Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Audio permission denied");
                Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (switch1.isChecked()) {
            switch1.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (switch1.isChecked()) {
            switch1.setChecked(false);
        }

    }


}

