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
import com.nature.stock.manager.NetManager;
import com.nature.stock.model.Item;
import com.nature.stock.model.Net;

import java.util.Arrays;
import java.util.List;

@PageView(name = "K线-复权明细", group = "股票", col = 0, row = 0)
public class NetDetailPage extends ListPage<Net> {

    @Injection
    private NetManager netManager;
    @Injection
    private WorkdayManager workDayManager;
    private final List<ExcelView.D<Net>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(this.getName()), C, S, CommonUtil.nullsLast(Net::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Net::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Net::getDate)),
            new ExcelView.D<>("最新", d -> TextUtil.price(d.getLatest()), C, E, CommonUtil.nullsLast(Net::getLatest)),
            new ExcelView.D<>("今开", d -> TextUtil.price(d.getOpen()), C, E, CommonUtil.nullsLast(Net::getOpen)),
            new ExcelView.D<>("最高", d -> TextUtil.price(d.getHigh()), C, E, CommonUtil.nullsLast(Net::getHigh)),
            new ExcelView.D<>("最低", d -> TextUtil.price(d.getLow()), C, E, CommonUtil.nullsLast(Net::getLow)),
            new ExcelView.D<>("平均-周", d -> TextUtil.price(d.getAvgWeek()), C, E, CommonUtil.nullsLast(Net::getAvgWeek)),
            new ExcelView.D<>("平均-月", d -> TextUtil.price(d.getAvgMonth()), C, E, CommonUtil.nullsLast(Net::getAvgMonth)),
            new ExcelView.D<>("平均-季", d -> TextUtil.price(d.getAvgSeason()), C, E, CommonUtil.nullsLast(Net::getAvgSeason)),
            new ExcelView.D<>("平均-年", d -> TextUtil.price(d.getAvgYear()), C, E, CommonUtil.nullsLast(Net::getAvgYear))
    );
    private Selector<String> start, end;
    private Item item;

    @Override
    protected List<ExcelView.D<Net>> define() {
        return ds;
    }

    @Override
    protected List<Net> listData() {
        String start = this.start.getValue();
        String end = this.end.getValue();
        return netManager.list(item.getCode(), item.getMarket(), start, end);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(start = template.selector(80, 30));
        searchBar.addConditionView(template.textView("-", 10, 30));
        searchBar.addConditionView(end = template.selector(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        this.item = this.getParam();
        List<String> list = workDayManager.listWorkDays(workDayManager.getLatestWorkDay());
        list.add(0, "");
        start.mapper(s -> s).init().refreshData(list);
        end.mapper(s -> s).init().refreshData(list);
    }

    private String getName() {
        return item.getName();
    }

}
