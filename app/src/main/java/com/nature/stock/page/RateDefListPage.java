package com.nature.stock.page;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.ListPage;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.stock.enums.RateDefType;
import com.nature.stock.manager.RateDefManager;
import com.nature.stock.model.RateDef;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@PageView(name = "涨幅定义配置", group = "股票", col = 0, row = 0)
public class RateDefListPage extends ListPage<RateDef> {

    @Injection
    private RateDefManager rateDefManager;
    private LinearLayout prop;
    private EditText code, title, days, dateStart, dateEnd;
    private Selector<RateDefType> type;
    private Button add;
    private final List<ExcelView.D<RateDef>> ds = Arrays.asList(
            new ExcelView.D<>("标题", d -> TextUtil.text(d.getTitle()), C, S),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, C),
            new ExcelView.D<>("类型", d -> TextUtil.text(RateDefType.codeToDesc(d.getType())), C, C),
            new ExcelView.D<>("天数", d -> TextUtil.text(d.getDays()), C, C),
            new ExcelView.D<>("开始日期", d -> TextUtil.text(d.getDateStart()), C, C),
            new ExcelView.D<>("截止日期", d -> TextUtil.text(d.getDateEnd()), C, C),
            new ExcelView.D<>("编辑", d -> "+", C, C, this.edit()),
            new ExcelView.D<>("删除", d -> "-", C, C, this.delete())
    );
    private String typeCode;
    private int width, height;
    private float density;

    @Override
    protected List<ExcelView.D<RateDef>> define() {
        return ds;
    }

    @Override
    protected List<RateDef> listData() {
        List<RateDef> list = rateDefManager.list(this.typeCode);
        list = list.stream().filter(i -> !ItemType.codes().contains(i.getCode())).collect(Collectors.toList());
        return list;
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        searchBar.addConditionView(add = template.button("+", 30, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        this.typeCode = this.getParam();
        add.setOnClickListener(v -> this.add());
    }

    private void add() {
        this.makeWindowStructure();
        PopUtil.confirm(context, "新增", prop, this::doAdd);
    }

    private Consumer<RateDef> edit() {
        return d -> {
            this.makeWindowStructure();
            this.code.setText(d.getCode());
            this.code.setFocusable(View.NOT_FOCUSABLE);
            this.title.setText(d.getTitle());
            this.type.setValue(RateDefType.codeToValue(d.getType()));
            this.days.setText(TextUtil.text(d.getDays()));
            this.dateStart.setText(d.getDateStart());
            this.dateEnd.setText(d.getDateEnd());
            PopUtil.confirm(context, "编辑-" + d.getTitle(), prop, () -> this.doEdit(d));
        };
    }

    private void doAdd() {
        this.doEdit((c) -> rateDefManager.find(this.typeCode, c) != null, RateDef::new);
    }

    private void doEdit(RateDef d) {
        this.doEdit((c) -> false, () -> d);
    }

    @SuppressLint("SimpleDateFormat")
    private void doEdit(Function<String, Boolean> fun, Supplier<RateDef> supplier) {
        String code = this.code.getText().toString();
        if (StringUtils.isBlank(code)) {
            throw new RuntimeException("编号不能为空");
        }
        if (fun.apply(code)) {
            throw new RuntimeException("CODE已存在");
        }
        String title = this.title.getText().toString();
        if (StringUtils.isBlank(title)) {
            throw new RuntimeException("标题不能为空");
        }
        String type = this.type.getValue().getCode();
        if (StringUtils.isBlank(type)) {
            throw new RuntimeException("请选择类型");
        }
        String dateStart = null;
        String dateEnd = null;
        Integer days = null;
        if (RateDefType.STATIC.getCode().equals(type)) {
            dateStart = this.dateStart.getText().toString();
            if (StringUtils.isNotBlank(dateStart)) {
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
            String s = this.days.getText().toString();
            if (StringUtils.isBlank(s)) {
                throw new RuntimeException("请填写天数");
            }
            if (!s.matches("^[0-9]*$")) {
                throw new RuntimeException("天数必须是数字");
            }
            days = Integer.valueOf(s);
        }
        RateDef def = supplier.get();
        def.setTypeCode(this.typeCode);
        def.setCode(code);
        def.setTitle(title);
        def.setType(type);
        def.setDays(days);
        def.setDateStart(dateStart);
        def.setDateEnd(dateEnd);
        rateDefManager.merge(def);
        PopUtil.alert(context, "保存成功");
        this.refreshData();
        PopUtil.alert(context, "编辑成功！");
    }

    private Consumer<RateDef> delete() {
        return d -> PopUtil.confirm(context, "删除-" + d.getTitle(), "确认删除吗？", () -> {
            rateDefManager.delete(this.typeCode, d.getCode());
            this.refreshData();
            PopUtil.alert(context, "删除成功！");
        });
    }

    private void makeWindowStructure() {
        prop = new LinearLayout(context);
        prop.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (width * 0.5d), (int) (height * 0.5d));
        prop.setLayoutParams(params);
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
        n4.addView(days = ViewUtil.editText(context));
        n5.addView(ViewUtil.textView(context, "开始日期："));
        n5.addView(dateStart = ViewUtil.editText(context));
        n6.addView(ViewUtil.textView(context, "结束日期："));
        n6.addView(dateEnd = ViewUtil.editText(context));
        type.mapper(i -> RateDefType.codeToDesc(i.getCode()));
        type.init().refreshData(Arrays.asList(RateDefType.values()));
        prop.addView(n1);
        prop.addView(n2);
        prop.addView(n3);
        prop.addView(n4);
        prop.addView(n5);
        prop.addView(n6);
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
