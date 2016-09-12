package com.ldxx.story;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WelcomeActivity extends AppCompatActivity {
    static String DB_PATH = "/data/data/com.ldxx.story/databases/";
    static String DB_NAME = "story.db";
    public static final String FIRST_IN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        check();
    }

    private void check() {
        SharedPreferences p = this.getPreferences(MODE_PRIVATE);
        if (p.getBoolean(FIRST_IN, true)) {
            try {
                copyDataBase();
                SharedPreferences.Editor editor = p.edit();
                editor.putBoolean(FIRST_IN, false);
                editor.commit();

                Toast.makeText(WelcomeActivity.this, "初始化数据成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                toMain();
            }

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toMain();
                }
            }, 2000);
        }
    }

    private void copyDataBase() throws IOException {
        String outFileName = DB_PATH + DB_NAME;
        File file = new File(DB_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = getAssets().open(DB_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    private void toMain() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
