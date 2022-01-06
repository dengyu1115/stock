package com.nature.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.nature.common.ioc.starter.ComponentStarter;
import com.nature.common.util.NotifyUtil;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.ViewTemplate;
import com.nature.func.config.FuncActivities;
import com.nature.stock.config.StockActivities;

import java.util.List;
import java.util.Map;

import static android.Manifest.permission.*;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private Context context;
    private LinearLayout page, body;
    private ViewTemplate template;
    private int height;
    private float density;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.initActivity(this);
        super.onCreate(savedInstanceState);
        this.verifyStoragePermissions(this);
        this.context = MainActivity.this;
        this.makeStructure();
        this.setContentView(page);
        this.showMain(FuncActivities.MENU);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) == 0) {
            ComponentStarter.getInstance().start();
            NotifyUtil.context = MainActivity.this;
        }
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        page = template.linearPage();
        page.setOrientation(LinearLayout.VERTICAL);
        height = metrics.heightPixels;
        density = metrics.density;
        this.header();
        this.body();
    }

    private void header() {
        LinearLayout header = new LinearLayout(context);
        page.addView(header);
        header.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (40 * density)));
        header.addView(this.tabBtn("配置", FuncActivities.MENU));
        header.addView(this.tabBtn("股票", StockActivities.MENU));
        header.addView(this.tabBtn("指数", null));
        header.addView(this.tabBtn("基金", null));
    }

    private void body() {
        this.body = new LinearLayout(context);
        page.addView(body);
        body.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height - (int) (40 * density)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return this.moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void verifyStoragePermissions(Activity activity) {
        // 检测是否有写的权限
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            String[] permissions = {MANAGE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void showMain(List<Map<String, Class<?>>> tag) {
        this.body.removeAllViews();
        if (tag == null) {
            return;
        }
        for (Map<String, Class<?>> map : tag) {
            this.listMenu(map);
        }
    }

    private void listMenu(Map<String, Class<?>> map) {
        LinearLayout line = template.line(200, MATCH_PARENT);
        line.setOrientation(LinearLayout.VERTICAL);
        this.body.addView(line);
        for (Map.Entry<String, Class<?>> entry : map.entrySet()) {
            line.addView(this.menuBtn(entry.getKey(), entry.getValue()));
            line.addView(template.textView("", 200, 5));
        }
    }

    private Button tabBtn(String name, List<Map<String, Class<?>>> tag) {
        Button btn = template.button(name, 80, 30);
        btn.setOnClickListener(v -> this.showMain(tag));
        return btn;
    }

    private Button menuBtn(String name, Class<?> clz) {
        Button btn = template.button(name, 100, 35);
        btn.setOnClickListener(v -> this.startActivity(new Intent(context, clz)));
        return btn;
    }

}
