package com.nature.stock.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.nature.stock.R;
import com.nature.stock.common.ioc.starter.ComponentStarter;
import com.nature.stock.common.service.TaskService;
import com.nature.stock.common.util.ClickUtil;
import com.nature.stock.common.util.NotifyUtil;
import com.nature.stock.common.util.ViewUtil;
import com.nature.stock.func.activity.*;
import com.nature.stock.item.activity.*;

import static android.Manifest.permission.*;

/**
 * 主入口activity
 *
 * @author nature
 * @version 1.0.0
 * @since 2019/12/16 22:51
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private Context context;
    private int count;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.initActivity(this);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.verifyStoragePermissions(this);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) == 0) {
            ComponentStarter.getInstance().start();
            NotifyUtil.context = MainActivity.this;
            this.context = MainActivity.this;
        } else {    // 首次启动进行组件注入
            finish();
        }
    }

    public void toWorkday(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, WorkdayActivity.class)));
    }

    public void testNotification(View view) {
        ClickUtil.asyncClick(view, () -> {
            NotifyUtil.doNotify(context, 0, "test title", "test content");
            return null;
        });
    }

    public void startTaskService(View view) {
        ClickUtil.doClick(view, () -> this.startService(new Intent(context, TaskService.class)));
    }

    public void stopTaskService(View view) {
        ClickUtil.doClick(view, () -> this.stopService(new Intent(context, TaskService.class)));
    }

    public void toTaskList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, TaskListActivity.class)));
    }

    public void toLineView(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, LineActivity.class)));
    }

    public void toManageGroup(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, GroupListActivity.class)));
    }

    public void toEditItemGroup(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, ItemGroupActivity.class)));
    }

    public void toMarkManage(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, MarkListActivity.class)));
    }

    public void toTaskManage(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, TaskManageActivity.class)));
    }

    public void toItemList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, ItemListActivity.class)));
    }

    public void toKlineList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, KlineListActivity.class)));
    }

    public void toBsList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, BuySellListActivity.class)));
    }

    public void toTargetList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, TargetListActivity.class)));
    }

    public void toItemQuota(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, ItemQuotaListActivity.class)));
    }

    public void toQuotaList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, IndexQuotaListActivity.class)));
    }

    public void toQuotaView(View view) {
        ClickUtil.doClick(view, () -> {
            Intent intent = new Intent(context, QuotaActivity.class);
            intent.putExtra("code", "000300");
            startActivity(intent);
        });
    }

    public void toDefineFundList(View view) {
        ClickUtil.doClick(view, () -> startActivity(new Intent(context, RateDefActivity.class)));
    }

    public void switchMain(View view) {
        String tag = (String) view.getTag();
        View viewFore = findViewById(R.id.fore);
        View viewBack = findViewById(R.id.back);
        if (count == 5 && "back".equals(tag)) {
            count = 0;
            viewFore.setVisibility(View.VISIBLE);
            viewBack.setVisibility(View.GONE);
        } else if (count == 5 && "fore".equals(tag)) {
            count = 0;
            viewBack.setVisibility(View.VISIBLE);
            viewFore.setVisibility(View.GONE);
        } else {
            count++;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            this.moveTaskToBack(true);
            return true;
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

}
