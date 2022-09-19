package com.nature.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.nature.common.ioc.starter.ComponentStarter;
import com.nature.common.page.BasicPage;
import com.nature.common.page.MainPage;
import com.nature.common.util.NotifyUtil;
import com.nature.common.util.ViewUtil;

import static android.Manifest.permission.*;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private BasicPage view;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.initActivity(this);
        super.onCreate(savedInstanceState);
        this.verifyStoragePermissions(this);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) == 0) {
            ComponentStarter.getInstance().start(this);
            NotifyUtil.context = MainActivity.this;
        }
        view = new BasicPage(this);
        this.setContentView(view);
        view.show(MainPage.class);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            int size = view.viewSize();
            if (size == 1) {
                return this.moveTaskToBack(true);
            } else {
                view.dispose();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void verifyStoragePermissions(Activity activity) {
        // 检测是否有写的权限
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            String[] permissions = {MANAGE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_EXTERNAL_STORAGE);
        }
    }


}
