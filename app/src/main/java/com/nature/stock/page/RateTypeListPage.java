package com.nature.stock.page;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.ListPage;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.ViewTemplate;
import com.nature.stock.manager.RateTypeManager;
import com.nature.stock.model.RateType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@PageView(name = "涨幅配置", group = "股票", col = 3, row = 1)
public class RateTypeListPage extends ListPage<RateType> {

    @Injection
    private RateTypeManager rateTypeManager;
    private EditText keyword;
    private LinearLayout prop;
    private EditText code, title;
    private Button add;
    private final List<ExcelView.D<RateType>> ds = Arrays.asList(
            new ExcelView.D<>("标题", d -> TextUtil.text(d.getTitle()), C, S, CommonUtil.nullsLast(RateType::getTitle)),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, C, CommonUtil.nullsLast(RateType::getCode)),
            new ExcelView.D<>("明细", d -> TextUtil.text("跳转"), C, C, this.toDetails()),
            new ExcelView.D<>("编辑", d -> "+", C, C, this.edit()),
            new ExcelView.D<>("删除", d -> "-", C, C, this.delete())
    );

    @Override
    protected List<ExcelView.D<RateType>> define() {
        return ds;
    }

    @Override
    protected List<RateType> listData() {
        List<RateType> list = rateTypeManager.list();
        String keyword = this.keyword.getText().toString();
        if (StringUtils.isNotBlank(keyword)) {
            list = list.stream().filter(i -> i.getTitle().contains(keyword)).collect(Collectors.toList());
        }
        list = list.stream().filter(i -> !ItemType.codes().contains(i.getCode())).collect(Collectors.toList());
        return list;
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(add = template.button("+", 30, 30));
        searchBar.addConditionView(keyword = template.editText(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        add.setOnClickListener(v -> this.add());
    }

    private void add() {
        this.makeWindowStructure();
        PopUtil.confirm(context, "新增", prop, this::doAdd);
    }

    private Consumer<RateType> edit() {
        return d -> {
            this.makeWindowStructure();
            this.code.setText(d.getCode());
            this.code.setFocusable(View.NOT_FOCUSABLE);
            this.title.setText(d.getTitle());
            PopUtil.confirm(context, "编辑-" + d.getTitle(), prop, () -> this.doEdit(d));
        };
    }

    private Consumer<RateType> toDetails() {
        return d -> this.show(RateDefListPage.class, d.getCode());
    }

    private void doAdd() {
        this.doEdit((c) -> rateTypeManager.find(c) != null, RateType::new);
    }

    private void doEdit(RateType d) {
        this.doEdit((c) -> false, () -> d);
    }

    private void doEdit(Function<String, Boolean> fun, Supplier<RateType> supplier) {
        String code = this.code.getText().toString();
        if (code.isEmpty()) {
            throw new RuntimeException("请填写编号");
        }
        if (fun.apply(code)) {
            throw new RuntimeException("CODE已存在");
        }
        String title = this.title.getText().toString();
        if (title.isEmpty()) {
            throw new RuntimeException("请填写标题");
        }
        RateType d = supplier.get();
        d.setCode(code);
        d.setTitle(title);
        rateTypeManager.merge(d);
        this.refreshData();
        PopUtil.alert(context, "编辑成功！");
    }

    private Consumer<RateType> delete() {
        return d -> PopUtil.confirm(context, "删除-" + d.getTitle(), "确认删除吗？", () -> {
            rateTypeManager.delete(d.getCode());
            this.refreshData();
            PopUtil.alert(context, "删除成功！");
        });
    }

    private void makeWindowStructure() {
        ViewTemplate template = ViewTemplate.build(context);
        prop = template.linearPage();
        prop.setGravity(Gravity.CENTER);
        LinearLayout cl = template.line(300, 30);
        LinearLayout nl = template.line(300, 30);
        LinearLayout el = template.line(300, 30);
        cl.addView(template.textView("CODE：", 100, 30));
        cl.addView(code = template.editText(200, 30));
        nl.addView(template.textView("标题：", 100, 30));
        nl.addView(title = template.editText(200, 30));
        prop.addView(cl);
        prop.addView(nl);
        prop.addView(el);
    }

}
