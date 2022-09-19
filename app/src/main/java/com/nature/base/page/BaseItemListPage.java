package com.nature.base.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.base.manager.BaseItemManager;
import com.nature.base.model.Item;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseItemListPage<T extends Item> extends ListPage<T> {

    protected EditText keyword;
    private Button reload;
    private final List<ExcelView.D<T>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(T::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(T::getCode)),
            new ExcelView.D<>("市场", d -> TextUtil.text(d.getMarket()), C, E, Sorter.nullsLast(T::getMarket))
    );

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("加载最新", 80, 30));
        this.extraViews(searchBar);
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", this.manager().reload());
                            this.refreshData();
                            return s;
                        })));
        this.extraBehaviours();
    }

    @Override
    protected List<ExcelView.D<T>> define() {
        List<ExcelView.D<T>> list = new ArrayList<>(ds);
        this.extraColumns(list);
        return list;
    }

    protected abstract BaseItemManager<T> manager();

    protected abstract void extraColumns(List<ExcelView.D<T>> list);

    protected abstract void extraViews(SearchBar searchBar);

    protected abstract void extraBehaviours();

}
