package com.nature.stock.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.manager.PeManager;
import com.nature.stock.model.Pe;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@PageView(name = "市盈率", group = "股票", col = 1, row = 7)
public class PeListPage extends ListPage<Pe> {

    private final List<ExcelView.D<Pe>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Pe::getName), this.detail()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Pe::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Pe::getDate)),
            new ExcelView.D<>("市盈率", d -> TextUtil.price(d.getPe()), C, E, CommonUtil.nullsLast(Pe::getPe))
    );
    @Injection
    private PeManager peManager;
    @Injection
    private WorkdayManager workDayManager;
    private Selector<String> date;
    private EditText keyword;
    private Button reload, load;

    @Override
    protected List<ExcelView.D<Pe>> define() {
        return ds;
    }

    @Override
    protected List<Pe> listData() {
        String date = this.date.getValue();
        String keyWord = this.keyword.getText().toString();
        return peManager.listByDate(date, keyWord);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("重新加载", 80, 30));
        searchBar.addConditionView(load = template.button("加载最新", 80, 30));
        searchBar.addConditionView(date = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        date.mapper(s -> s).init().refreshData(workDayManager.listWorkDays(workDayManager.getLatestWorkDay()));
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", peManager.reload());
                            this.refreshData();
                            return s;
                        })));
        load.setOnClickListener(v -> ClickUtil.asyncClick(v, () -> {
            String s = String.format("加载完成,共%s条", peManager.load());
            this.refreshData();
            return s;
        }));
    }

    private Consumer<Pe> detail() {
        return d -> {
            this.show(PeDetailPage.class, d);
        };
    }

}
