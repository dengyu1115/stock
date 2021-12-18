package com.nature.stock.item.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.stock.common.util.InstanceHolder;
import com.nature.stock.common.util.ViewUtil;
import com.nature.stock.common.view.KlineView;
import com.nature.stock.item.manager.KlineManager;
import com.nature.stock.item.model.Kline;

import java.util.List;

/**
 * K线图
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:11
 */
public class KlineActivity extends AppCompatActivity {

    private final KlineManager klineManager = InstanceHolder.get(KlineManager.class);

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KlineView klineView = new KlineView(this);
        klineView.data(this.data());
        this.setContentView(klineView);
        ViewUtil.initActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private List<Kline> data() {
        String code = this.getIntent().getStringExtra("code");
        String market = this.getIntent().getStringExtra("market");
        return klineManager.list(code, market);
    }

}
