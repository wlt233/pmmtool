package moe.tqlwsl.pmmtool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.init_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSoPath();
            }
        });
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private void initSoPath() {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk <= 29) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "The module needs permission!", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                writeSdcard();
            }
        }
        else {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            } else {
                writeSdcard();
            }
        }
    }

    private void writeSdcard()  {
        String text = "";
        PackageManager pm = getPackageManager();
        List<PackageInfo> pkgList = pm.getInstalledPackages(0);
        if(pkgList.size() > 0) {
            for (PackageInfo pi : pkgList) {
                if (pi.applicationInfo.publicSourceDir.contains("moe.tqlwsl.pmmtool")) {
                    text = pi.applicationInfo.publicSourceDir.replace("base.apk", "lib/arm64/libpmm.so");
                }
            }
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file1 = new File("/sdcard/pmmtool/","soPath.txt");
                if (!file1.exists()) {
                    try {
                        File folder = new File("/sdcard/", "pmmtool");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        file1.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file1);
                    fileOutputStream.write(text.getBytes());
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
