package com.nature.item.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.common.enums.DefaultQuota;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.QuotaView;
import com.nature.common.view.ViewTemplate;
import com.nature.item.manager.QuotaManager;
import com.nature.item.model.Quota;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 指标数据
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:26
 */
public class QuotaActivity extends AppCompatActivity {

    private final QuotaManager quotaManager = InstanceHolder.get(QuotaManager.class);

    private Context context;
    private int width, height;
    private QuotaView view;
    private LinearLayout page;
    private ViewTemplate template;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = QuotaActivity.this;
        this.makeStructure();
        view.data(this.refreshData(this.getCode()));
        this.setContentView(page);
        ViewUtil.initActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private String getCode() {
        String code = "000300";
        String extra = this.getIntent().getStringExtra("code");
        if (StringUtils.isNotBlank(extra)) {
            code = extra;
        }
        return code;
    }


    public void makeStructure() {
        template = ViewTemplate.build(context);
        page = template.linearPage();
        page.setOrientation(LinearLayout.HORIZONTAL);
        page.setGravity(Gravity.CENTER);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        LinearLayout n1 = this.line(3);
        LinearLayout n2 = this.line(1);
        view = new QuotaView(context);
        n2.addView(view);
        page.addView(n2);
        page.addView(n1);
        n1.setOrientation(LinearLayout.VERTICAL);
        n1.addView(this.handleLine());
        n1.addView(this.button(DefaultQuota.HS));
        n1.addView(this.button(DefaultQuota.SZ));
        n1.addView(this.button(DefaultQuota.SC));
        n1.addView(this.button(DefaultQuota.ZXB));
        n1.addView(this.button(DefaultQuota.CYB));
    }

    private LinearLayout handleLine() {
        LinearLayout line = template.line((int) (width * 0.2d), (int) (height * 0.1d));
        Button reload = template.button("重新加载", 80, 30);
        Button loadLatest = template.button("加载最新", 80, 30);
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> String.format("加载完成,共%s条", quotaManager.reloadAll()))
                )
        );
        loadLatest.setOnClickListener(v ->
                ClickUtil.asyncClick(v, () -> String.format("加载完成,共%s条", quotaManager.loadLatest())));
        line.addView(reload);
        line.addView(loadLatest);
        return line;
    }

    private LinearLayout line(int weight) {
        LinearLayout line = new LinearLayout(context);
        line.setGravity(Gravity.CENTER);
        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        param.weight = weight;
        line.setLayoutParams(param);
        return line;
    }

    private Button button(DefaultQuota quota) {
        Button button = new Button(context);
        button.setText(quota.getName());
        LayoutParams param = new LayoutParams((int) (width * 0.2d), (int) (height * 0.1d));
        button.setLayoutParams(param);
        button.setPadding(10, 10, 10, 10);
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(v -> view.data(this.refreshData(quota.getCode())));
        return button;
    }

    private List<Quota> refreshData(String code) {
        return quotaManager.listByCode(code);
    }

}
