package com.nature.stock.page;

import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.ListPage;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.manager.PeManager;
import com.nature.stock.model.Pe;

import java.util.Arrays;
import java.util.List;

@PageView(name = "市盈率明细", group = "股票", col = 0, row = 0)
public class PeDetailPage extends ListPage<Pe> {

    @Injection
    private PeManager peManager;
    @Injection
    private WorkdayManager workDayManager;
    private Selector<String> start, end;
    private Pe pe;
    private final List<ExcelView.D<Pe>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(this.getName()), C, S, CommonUtil.nullsLast(Pe::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Pe::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Pe::getDate)),
            new ExcelView.D<>("市盈率", d -> TextUtil.price(d.getPe()), C, E, CommonUtil.nullsLast(Pe::getPe))
    );

    @Override
    protected List<ExcelView.D<Pe>> define() {
        return ds;
    }

    @Override
    protected List<Pe> listData() {
        String start = this.start.getValue();
        String end = this.end.getValue();
        return peManager.list(pe.getCode(), pe.getMarket(), start, end);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(start = template.selector(80, 30));
        searchBar.addConditionView(template.textView("-", 10, 30));
        searchBar.addConditionView(end = template.selector(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        this.pe = this.getParam();
        List<String> list = workDayManager.listWorkDays(workDayManager.getLatestWorkDay());
        list.add(0, "");
        start.mapper(s -> s).init().refreshData(list);
        end.mapper(s -> s).init().refreshData(list);
    }

    private String getName() {
        return pe.getName();
    }

}
