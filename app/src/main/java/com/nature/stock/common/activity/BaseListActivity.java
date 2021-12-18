package com.nature.stock.common.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.stock.common.util.ViewTemplate;
import com.nature.stock.common.util.ViewUtil;
import com.nature.stock.common.view.ExcelView;
import com.nature.stock.common.view.SearchBar;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 列表展示页面基类
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 18:56
 */
public abstract class BaseListActivity<T> extends AppCompatActivity {

    protected static final int C = 0, S = 1, E = 2;
    protected Context context;
    protected ViewTemplate template;
    private LinearLayout page;
    private ExcelView<T> excel;
    private Button button;
    private TextView total;
    private final Handler handler = new Handler(msg -> {
        this.total.setText(String.valueOf(this.excel.getListSize()));
        return false;
    });
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = BaseListActivity.this;
        this.makeStructure();
        this.initBehaviours();
        this.setContentView(page);
        ViewUtil.initActivity(this);
        this.refreshData();
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        page = template.linearPage();
        page.setOrientation(LinearLayout.VERTICAL);
        height = metrics.heightPixels;
        this.header();
        this.body();
        this.footer();
    }

    private void initBehaviours() {
        this.button.setOnClickListener(v -> this.refreshData());
        this.excel.define(this.define());
        this.initHeaderBehaviours();
    }

    private void header() {
        LinearLayout header = new LinearLayout(context);
        page.addView(header);
        header.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (height * 0.05)));
        SearchBar searchBar = new SearchBar(context);
        header.addView(searchBar);
        button = template.button("查询", 50, 30);
        searchBar.addHandleView(button);
        this.initHeaderViews(searchBar);
    }

    private void body() {
        LinearLayout body = new LinearLayout(context);
        page.addView(body);
        body.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (height * 0.9)));
        this.excel = new ExcelView<>(context, 5);
        body.addView(this.excel);
    }

    private void footer() {
        LinearLayout footer = new LinearLayout(context);
        page.addView(footer);
        footer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (height * 0.05)));
        footer.setGravity(Gravity.CENTER);
        total = new TextView(context);
        footer.addView(total);
        total.setGravity(Gravity.CENTER);
    }

    protected void refreshData() {
        new Thread(() -> {
            this.excel.data(this.listData());
            this.refreshTotal();
        }).start();
    }

    private void refreshTotal() {
        handler.sendMessage(new Message());
    }

    protected abstract List<ExcelView.D<T>> define();

    protected abstract List<T> listData();

    protected abstract void initHeaderViews(SearchBar searchBar);

    protected abstract void initHeaderBehaviours();

}
