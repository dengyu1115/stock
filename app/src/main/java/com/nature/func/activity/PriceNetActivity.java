package com.nature.func.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.PriceNetView;
import com.nature.func.manager.PriceNetManager;
import com.nature.func.model.PriceNet;

import java.util.List;

/**
 * 债券线图
 * @author nature
 * @version 1.0.0
 * @since 2020/4/5 15:46
 */
public class PriceNetActivity extends AppCompatActivity {

    private final PriceNetManager priceNetManager = InstanceHolder.get(PriceNetManager.class);

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PriceNetView priceNetView = new PriceNetView(this);
        priceNetView.data(this.data());
        this.setContentView(priceNetView);
        ViewUtil.initActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private List<PriceNet> data() {
        String code = this.getIntent().getStringExtra("code");
        return priceNetManager.listByCode(code);
    }

}
