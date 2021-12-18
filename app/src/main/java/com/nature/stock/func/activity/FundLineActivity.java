package com.nature.stock.func.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;
import com.nature.stock.common.manager.LineManager;
import com.nature.stock.common.model.LineDef;
import com.nature.stock.common.util.InstanceHolder;
import com.nature.stock.common.util.ViewUtil;
import com.nature.stock.common.view.LineView;
import com.nature.stock.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * 基金折线图
 * @author nature
 * @version 1.0.0
 * @since 2020/10/18 14:34
 */
public class FundLineActivity extends AppCompatActivity {

    private final LineManager lineManager = InstanceHolder.get(LineManager.class);

    private LineView view;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new LineView(this);
        this.setContentView(view);
        ViewUtil.initActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.refresh();
    }

    private void refresh() {
        String json = this.getIntent().getStringExtra("fund");
        Item fund = JSON.parseObject(json, Item.class);
        if (fund == null) return;
        List<LineDef> list = new ArrayList<>();
        LineDef d1 = new LineDef();
        d1.setTitle(fund.getName());
        d1.setColor(Color.BLUE);
        d1.setSql("select date, net_total price from net where code = '" + fund.getCode() + "'");
        list.add(d1);
        list.add(this.def("沪深300", "000300", "1", Color.RED));
        list.add(this.def("创业板", "399006", "0", Color.GREEN));
        list.add(this.def("中小板", "399005", "0", Color.GRAY));
        list.stream().parallel().forEach(i -> i.setList(lineManager.list(i.getSql())));
        lineManager.format(list, null);
        for (LineDef def : list) {
            lineManager.calculate(def.getList());
        }
        view.data(list);
    }

    private LineDef def(String title, String code, String market, int color) {
        LineDef def = new LineDef();
        def.setTitle(title);
        def.setColor(color);
        def.setSql("select date, latest price from kline where code = '" + code + "' and market = '" + market + "'");
        return def;
    }

}
