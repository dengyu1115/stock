package com.nature.stock.common.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.stock.common.manager.LineManager;
import com.nature.stock.common.manager.StrategyManager;
import com.nature.stock.common.model.LineDef;
import com.nature.stock.common.model.Strategy;
import com.nature.stock.common.util.InstanceHolder;
import com.nature.stock.common.util.PopUtil;
import com.nature.stock.common.util.ViewTemplate;
import com.nature.stock.common.util.ViewUtil;
import com.nature.stock.common.view.LineView;
import com.nature.stock.common.view.Selector;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 折线图
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:12
 */
public class LineActivity extends AppCompatActivity {

    private final LineManager lineManager = InstanceHolder.get(LineManager.class);

    private final StrategyManager strategyManager = InstanceHolder.get(StrategyManager.class);
    private final Map<Integer, Button> bs = new LinkedHashMap<>();
    private Context context;
    private LinearLayout page;
    private ViewTemplate template;
    private int width, height;
    private EditText code, name, date, title, sql;
    private Selector<Strategy> selector;
    private LineView view;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = LineActivity.this;
        this.makeStructure();
        this.setContentView(page);
        ViewUtil.initActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void refresh() {
        Strategy value = this.selector.getValue();
        Strategy strategy = strategyManager.findByCode(value.getCode());
        List<LineDef> list = strategy.getList();
        if (list == null) list = new ArrayList<>();
        list.stream().parallel().forEach(i -> i.setList(lineManager.list(i.getSql())));
        lineManager.format(list, date.getText().toString());
        for (LineDef def : list) {
            lineManager.calculate(def.getList());
        }
        this.view.data(list);
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        page = template.linearPage();
        page.setOrientation(LinearLayout.HORIZONTAL);
        page.setGravity(Gravity.CENTER);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        this.body();
    }

    private void body() {
        LinearLayout n1 = this.line(7);
        LinearLayout n2 = this.line(2);
        view = new LineView(context);
        n2.addView(view);
        page.addView(n2);
        page.addView(n1);
        n1.setOrientation(LinearLayout.VERTICAL);
        selector = template.selector(100, 30);
        this.refreshStrategySelector();
        LinearLayout line = this.line();
        line.addView(this.button("+", v ->
                PopUtil.confirm(context, "添加策略", this.strategy(), () -> {
                    String code = this.code.getText().toString();
                    if (StringUtils.isBlank(code)) {
                        throw new RuntimeException("请填写编号");
                    }
                    String name = this.name.getText().toString();
                    if (StringUtils.isBlank(name)) {
                        throw new RuntimeException("请填写名称");
                    }
                    Strategy d = new Strategy();
                    d.setCode(code);
                    d.setName(name);
                    strategyManager.merge(d);
                    this.refreshStrategySelector();
                    PopUtil.alert(context, "添加完成");
                })));
        line.addView(this.button("-", v ->
                PopUtil.confirm(context, "删除策略", "确定删除策略吗？", () -> {
                    strategyManager.delete(selector.getValue().getCode());
                    this.refreshStrategySelector();
                    PopUtil.alert(context, "删除完成");
                })));
        line.addView(selector);
        line.addView(this.button("⬇", v ->
                PopUtil.confirm(context, "载入策略", "确定载入策略吗？", () -> {
                    Strategy strategy = selector.getValue();
                    if (strategy == null) {
                        throw new RuntimeException("请先选择策略");
                    }
                    List<LineDef> list = strategy.getList();
                    if (list == null) {
                        for (Map.Entry<Integer, Button> entry : bs.entrySet()) {
                            Button button = entry.getValue();
                            button.setTag(null);
                            button.setText("+");
                        }
                    } else {
                        Map<Integer, LineDef> map = list.stream().collect(Collectors.toMap(LineDef::getColor, i -> i));
                        for (Map.Entry<Integer, Button> entry : bs.entrySet()) {
                            Integer color = entry.getKey();
                            Button button = entry.getValue();
                            LineDef def = map.get(color);
                            if (def == null) {
                                button.setTag(null);
                                button.setText("+");
                            } else {
                                button.setTag(def);
                                button.setText(def.getTitle());
                            }
                        }
                    }
                    this.refresh();
                    PopUtil.alert(context, "完成");
                })));
        line.addView(this.button("⬆", v ->
                PopUtil.confirm(context, "策略入库", "确定入库策略吗？", () -> {
                    Strategy strategy = selector.getValue();
                    if (strategy == null) {
                        throw new RuntimeException("请先选择策略");
                    }
                    List<LineDef> list = new ArrayList<>();
                    for (Button button : bs.values()) {
                        LineDef def = (LineDef) button.getTag();
                        if (def == null) continue;
                        list.add(def);
                    }
                    strategy.setList(list);
                    strategyManager.merge(strategy);
                    this.refresh();
                    PopUtil.alert(context, "完成");
                })));
        LinearLayout dl = this.line();
        dl.addView(template.textView("日期", 50, 30));
        dl.addView(date = template.editText(100, 30));
        n1.addView(line);
        n1.addView(dl);
        this.genLineDefButton(n1, Color.RED);
        this.genLineDefButton(n1, Color.GREEN);
        this.genLineDefButton(n1, Color.BLUE);
        this.genLineDefButton(n1, Color.LTGRAY);
    }

    private void refreshStrategySelector() {
        selector.mapper(Strategy::getName).init().refreshData(strategyManager.list());
    }

    private void genLineDefButton(LinearLayout line, int color) {
        Button rb = this.button(color);
        bs.put(color, rb);
        line.addView(rb);
    }

    private LinearLayout line() {
        LinearLayout line = new LinearLayout(context);
        line.setGravity(Gravity.CENTER);
        line.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.1d));
        line.setLayoutParams(param);
        return line;
    }

    private LinearLayout line(int weight) {
        LinearLayout line = new LinearLayout(context);
        line.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        param.weight = weight;
        line.setLayoutParams(param);
        return line;
    }

    private Button button(String name, View.OnClickListener listener) {
        Button button = new Button(context);
        button.setText(name);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((int) (height * 0.06d), (int) (height * 0.07d));
        button.setLayoutParams(param);
        button.setPadding(10, 10, 10, 10);
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(listener);
        return button;
    }

    private Button button(int color) {
        Button button = new Button(context);
        button.setText("+");
        button.setBackgroundColor(color);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((int) (width * 0.2d), (int) (height * 0.1d));
        button.setLayoutParams(param);
        button.setPadding(10, 10, 10, 10);
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(v ->
                PopUtil.confirm(context, "折线定义编辑", definition(), () -> {
                    String title = this.title.getText().toString();
                    String sql = this.sql.getText().toString();
                    if (StringUtils.isBlank(title) && StringUtils.isNotBlank(sql)) {
                        throw new RuntimeException("请填写标题");
                    }
                    if (StringUtils.isNotBlank(title) && StringUtils.isBlank(sql)) {
                        throw new RuntimeException("请填写sql");
                    }
                    if (StringUtils.isNotBlank(title)) {
                        LineDef def = new LineDef();
                        def.setColor(color);
                        def.setTitle(title);
                        def.setSql(sql);
                        button.setTag(def);
                        button.setText(title);
                    } else {
                        button.setTag(null);
                        button.setText("+");
                    }
                }));
        return button;
    }

    private LinearLayout areaLine() {
        LinearLayout line = new LinearLayout(context);
        line.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.3d));
        line.setLayoutParams(param);
        return line;
    }

    private LinearLayout definition() {
        LinearLayout line = new LinearLayout(context);
        line.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (width * 0.5d), (int) (height * 0.5d));
        line.setLayoutParams(params);
        LinearLayout cl = this.line();
        LinearLayout rl = this.areaLine();
        cl.addView(template.textView("标题：", 50, 30));
        cl.addView(title = template.editText(200, 30));
        rl.addView(template.textView("SQL：", 50, 30));
        rl.addView(sql = template.areaText(200, 90));
        line.addView(cl);
        line.addView(rl);
        return line;
    }

    private LinearLayout strategy() {
        LinearLayout line = new LinearLayout(context);
        line.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (width * 0.5d), (int) (height * 0.5d));
        line.setLayoutParams(params);
        LinearLayout cl = this.line();
        LinearLayout nl = this.line();
        cl.addView(template.textView("编号：", 50, 30));
        cl.addView(code = template.editText(200, 30));
        nl.addView(template.textView("名称：", 50, 30));
        nl.addView(name = template.editText(200, 30));
        line.addView(cl);
        line.addView(nl);
        return line;
    }

}
