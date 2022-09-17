package com.nature.stock.page;

import android.widget.Button;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.stock.manager.IndustryManager;
import com.nature.stock.model.Industry;

import java.util.Arrays;
import java.util.List;

@PageView(name = "行业", group = "股票", col = 1, row = 1)
public class IndustryListPage extends ListPage<Industry> {
    @Injection
    private IndustryManager industryManager;
    private final List<ExcelView.D<Industry>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(Industry::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(Industry::getCode))
    );
    private Button reload;

    protected List<Industry> listData() {
        return industryManager.list();
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("加载最新", 80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", industryManager.reload());
                            this.refreshData();
                            return s;
                        })));
    }

    @Override
    protected List<ExcelView.D<Industry>> define() {
        return ds;
    }

}
