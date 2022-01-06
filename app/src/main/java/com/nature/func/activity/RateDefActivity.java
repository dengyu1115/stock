package com.nature.func.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;
import com.nature.common.constant.DefType;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.func.manager.DefinitionManager;
import com.nature.func.model.Definition;
import com.nature.common.util.*;
import com.nature.common.view.ExcelView;
import com.nature.common.view.Selector;
import com.nature.func.manager.FundListDefManager;
import com.nature.func.model.RateDef;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 基金涨幅定义
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 18:58
 */
public class RateDefActivity extends AppCompatActivity {

    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int C = 0, S = 1, E = 2;
    private final DefinitionManager definitionManager = InstanceHolder.get(DefinitionManager.class);
    private final FundListDefManager fundListDefManager = InstanceHolder.get(FundListDefManager.class);
    private Context context;
    private LinearLayout page;
    private int width, height;
    private ExcelView<RateDef> excel;

    private Selector<Definition> selector;
    private final List<ExcelView.D<RateDef>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getTitle()), C, S, Sorter.nullsLast(RateDef::getTitle)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(RateDef::getCode)),
            new ExcelView.D<>("类型", d -> TextUtil.text(fundListDefManager.getTypeName(d.getType())), C, E, Sorter.nullsLast(RateDef::getType)),
            new ExcelView.D<>("数量", d -> TextUtil.text(d.getCount()), C, E, Sorter.nullsLast(RateDef::getCount)),
            new ExcelView.D<>("开始日期", d -> TextUtil.text(d.getDateStart()), C, E, Sorter.nullsLast(RateDef::getDateStart)),
            new ExcelView.D<>("结束日期", d -> TextUtil.text(d.getDateEnd()), C, E, Sorter.nullsLast(RateDef::getDateEnd)),
            new ExcelView.D<>("操作", d -> "-", C, C, this::popDelDef)
    );
    private Selector<String> type;
    private EditText code, title, count, dateStart, dateEnd;
    private Button add, delete, addDef;
    private LinearLayout header, body, footer;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = RateDefActivity.this;
        this.makeStructure();
        this.setContentView(page);
        ViewUtil.initActivity(this);
        this.initFunctions();
    }

    private void initFunctions() {
        selector.mapper(Definition::getTitle).onChangeRun(this::refreshExcel).init();
        this.refreshSelector();
        add.setOnClickListener(v -> popEditRule());
        delete.setOnClickListener(v -> popDelRule());
        addDef.setOnClickListener(v -> popEditDef());
    }

    private void popDelRule() {
        PopUtil.confirm(context, "删除显示规则", "确定要删除吗？", () -> {
            Definition definition = selector.getValue();
            if (definition == null) {
                throw new RuntimeException("请选择要删除的规则");
            }
            definitionManager.delete(DefType.FUND_LIST, definition.getCode());
            PopUtil.alert(context, "删除成功");
            this.refreshSelector();
        });
    }

    private void popEditRule() {
        PopUtil.confirm(context, "添加显示规则", popWindow(), () -> {
            String code = this.code.getText().toString();
            if (StringUtils.isBlank(code)) {
                throw new RuntimeException("编号不能为空");
            }
            String title = this.title.getText().toString();
            if (StringUtils.isBlank(title)) {
                throw new RuntimeException("标题不能为空");
            }
            Definition definition = new Definition();
            definition.setType(DefType.FUND_LIST);
            definition.setCode(code);
            definition.setTitle(title);
            definitionManager.merge(definition);
            PopUtil.alert(context, "保存成功");
            this.refreshSelector();
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void popEditDef() {
        PopUtil.confirm(context, "添加显示规则", editShow(), () -> {
            Definition definition = selector.getValue();
            if (definition == null) {
                throw new RuntimeException("请选择规则");
            }
            String code = this.code.getText().toString();
            if (StringUtils.isBlank(code)) {
                throw new RuntimeException("编号不能为空");
            }
            String title = this.title.getText().toString();
            if (StringUtils.isBlank(title)) {
                throw new RuntimeException("标题不能为空");
            }
            String type = this.type.getValue();
            if (StringUtils.isBlank(type)) {
                throw new RuntimeException("请选择类型");
            }
            String dateStart = null;
            String dateEnd = null;
            Integer count = null;
            if (FundListDefManager.DEFINED.equals(type)) {
                dateStart = this.dateStart.getText().toString();
                if (StringUtils.isBlank(dateStart)) {
                    throw new RuntimeException("请填写开始日期");
                } else {
                    try {
                        new SimpleDateFormat("yyyy-MM-dd").parse(dateStart);
                    } catch (ParseException e) {
                        throw new RuntimeException("请填写正确的日期格式");
                    }
                }
                dateEnd = this.dateEnd.getText().toString();
                if (StringUtils.isNotBlank(dateEnd)) {
                    try {
                        new SimpleDateFormat("yyyy-MM-dd").parse(dateEnd);
                    } catch (ParseException e) {
                        throw new RuntimeException("请填写正确的日期格式");
                    }
                }
            } else {
                String s = this.count.getText().toString();
                if (StringUtils.isBlank(s)) {
                    throw new RuntimeException("请填写数量");
                }
                if (!s.matches("^[0-9]*$")) {
                    throw new RuntimeException("数量必须是数字");
                }
                count = Integer.valueOf(s);
            }
            String json = definition.getJson();
            List<RateDef> list;
            if (StringUtils.isBlank(json)) {
                list = new ArrayList<>();
            } else {
                list = JSON.parseArray(json, RateDef.class);
            }
            RateDef def = new RateDef();
            def.setCode(code);
            def.setTitle(title);
            def.setType(type);
            def.setCount(count);
            def.setDateStart(dateStart);
            def.setDateEnd(dateEnd);
            list.add(def);
            list.sort(Comparator.comparing(RateDef::getCode));
            definition.setJson(JSON.toJSONString(list));
            definitionManager.merge(definition);
            PopUtil.alert(context, "保存成功");
            this.refreshExcel();
        });
    }

    private void popDelDef(RateDef o) {
        PopUtil.confirm(context, "删除显示规则", "确定删除 " + o.getTitle() + " 吗？", () -> {
            Definition definition = selector.getValue();
            if (definition == null) {
                throw new RuntimeException("请选择规则");
            }
            List<RateDef> list = JSON.parseArray(definition.getJson(), RateDef.class);
            for (RateDef def : list) {
                if (def.getCode().equals(o.getCode())) {
                    list.remove(def);
                    break;
                }
            }
            definition.setJson(JSON.toJSONString(list));
            definitionManager.merge(definition);
            PopUtil.alert(context, "删除成功");
            this.refreshExcel();
        });
    }

    private void refreshSelector() {
        selector.refreshData(definitionManager.list(DefType.FUND_LIST));
    }

    private void refreshExcel() {
        Definition d = selector.getValue();
        if (d == null) return;
        String json = d.getJson();
        if (StringUtils.isBlank(json)) excel.data(new ArrayList<>());
        else excel.data(JSON.parseArray(json, RateDef.class));
    }

    private void refreshType() {
        type.refreshData(fundListDefManager.listAllType());
    }

    private void makeStructure() {
        this.initMainStructure();
        excel = new ExcelView<>(context, 8);
        excel.define(ds);
        selector = new Selector<>(context);
        add = ViewUtil.button(context, "添加");
        delete = ViewUtil.button(context, "删除");
        addDef = ViewUtil.button(context, "添加");
        header.addView(add);
        header.addView(selector);
        header.addView(delete);
        body.addView(excel);
        footer.addView(addDef);
    }

    /**
     * 初始化主要页面架构
     */
    private void initMainStructure() {
        page = new LinearLayout(context);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        header = new LinearLayout(context);
        body = new LinearLayout(context);
        footer = new LinearLayout(context);
        header.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (density * 40)));
        header.setGravity(Gravity.CENTER);
        body.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height - (int) (density * 70)));
        footer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (density * 30)));
        footer.setGravity(Gravity.CENTER);
        page.addView(header);
        page.addView(body);
        page.addView(footer);
    }

    private LinearLayout popWindow() {
        LinearLayout line = new LinearLayout(context);
        line.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (width * 0.5d), (int) (height * 0.5d));
        line.setLayoutParams(params);
        LinearLayout cl = this.line();
        LinearLayout nl = this.line();
        cl.addView(ViewUtil.textView(context, "编号："));
        cl.addView(code = ViewUtil.editText(context));
        nl.addView(ViewUtil.textView(context, "标题："));
        nl.addView(title = ViewUtil.editText(context));
        line.addView(cl);
        line.addView(nl);
        return line;
    }

    private LinearLayout editShow() {
        LinearLayout line = new LinearLayout(context);
        line.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (width * 0.5d), (int) (height * 0.5d));
        line.setLayoutParams(params);
        LinearLayout n1 = this.line();
        LinearLayout n2 = this.line();
        LinearLayout n3 = this.line();
        LinearLayout n4 = this.line();
        LinearLayout n5 = this.line();
        LinearLayout n6 = this.line();
        n1.addView(ViewUtil.textView(context, "编号："));
        n1.addView(code = ViewUtil.editText(context));
        n2.addView(ViewUtil.textView(context, "标题："));
        n2.addView(title = ViewUtil.editText(context));
        n3.addView(ViewUtil.textView(context, "类型："));
        n3.addView(type = new Selector<>(context));
        n4.addView(ViewUtil.textView(context, "数量："));
        n4.addView(count = ViewUtil.editText(context));
        n5.addView(ViewUtil.textView(context, "开始日期："));
        n5.addView(dateStart = ViewUtil.editText(context));
        n6.addView(ViewUtil.textView(context, "结束日期："));
        n6.addView(dateEnd = ViewUtil.editText(context));
        type.mapper(fundListDefManager::getTypeName);
        type.init();
        this.refreshType();
        line.addView(n1);
        line.addView(n2);
        line.addView(n3);
        line.addView(n4);
        line.addView(n5);
        line.addView(n6);
        return line;
    }

    private LinearLayout line() {
        LinearLayout line = new LinearLayout(context);
        line.setGravity(Gravity.CENTER);
        line.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, (int) (density * 32));
        line.setLayoutParams(param);
        return line;
    }

}
