package com.nature.common.page;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import com.nature.common.view.ViewTemplate;
import com.nature.func.config.FuncPages;
import com.nature.stock.config.StockPages;

import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainPage extends Page {

    private Context context;
    private LinearLayout page, body;
    private ViewTemplate template;
    private int height;
    private float density;

    @Override
    protected void makeStructure(LinearLayout page, Context context) {
        this.context = context;
        this.page = page;
        this.makeStructure();
    }

    @Override
    protected void onShow() {
        this.showMain(FuncPages.MENU);
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
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
        header.addView(this.tabBtn("配置", FuncPages.MENU));
        header.addView(this.tabBtn("股票", StockPages.MENU));
        header.addView(this.tabBtn("指数", null));
        header.addView(this.tabBtn("基金", null));
    }

    private void body() {
        this.body = new LinearLayout(context);
        page.addView(body);
        body.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height - (int) (40 * density)));
    }

    private void showMain(List<Map<String, Class<? extends Page>>> tag) {
        this.body.removeAllViews();
        if (tag == null) {
            return;
        }
        for (Map<String, Class<? extends Page>> map : tag) {
            this.listMenu(map);
        }
    }

    private void listMenu(Map<String, Class<? extends Page>> map) {
        LinearLayout line = template.line(200, MATCH_PARENT);
        line.setOrientation(LinearLayout.VERTICAL);
        this.body.addView(line);
        for (Map.Entry<String, Class<? extends Page>> entry : map.entrySet()) {
            line.addView(this.menuBtn(entry.getKey(), entry.getValue()));
            line.addView(template.textView("", 200, 5));
        }
    }

    private Button tabBtn(String name, List<Map<String, Class<? extends Page>>> tag) {
        Button btn = template.button(name, 80, 30);
        btn.setOnClickListener(v -> this.showMain(tag));
        return btn;
    }

    private Button menuBtn(String name, Class<? extends Page> clz) {
        Button btn = template.button(name, 100, 35);
        btn.setOnClickListener(v -> this.show(clz));
        return btn;
    }
}
