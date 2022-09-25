package com.nature.base.page;

import com.nature.base.manager.BaseNetManager;
import com.nature.base.model.Item;
import com.nature.base.model.Net;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.page.ListPage;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;

import java.util.Arrays;
import java.util.List;

public abstract class BaseNetDetailPage<T extends Item> extends ListPage<Net> {

    @Injection
    private WorkdayManager workDayManager;
    private Selector<String> start, end;
    private Item item;
    private final List<ExcelView.D<Net>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(this.getName()), C, S, CommonUtil.nullsLast(Net::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Net::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Net::getDate)),
            new ExcelView.D<>("累计", C, Arrays.asList(
                    new ExcelView.D<>("最新", d -> TextUtil.price(d.getNet().getLatest()), C, E, CommonUtil.nullsLast(d -> d.getNet().getLatest())),
                    new ExcelView.D<>("今开", d -> TextUtil.price(d.getNet().getOpen()), C, E, CommonUtil.nullsLast(d -> d.getNet().getOpen())),
                    new ExcelView.D<>("最高", d -> TextUtil.price(d.getNet().getHigh()), C, E, CommonUtil.nullsLast(d -> d.getNet().getHigh())),
                    new ExcelView.D<>("最低", d -> TextUtil.price(d.getNet().getLow()), C, E, CommonUtil.nullsLast(d -> d.getNet().getLow()))
            )),
            new ExcelView.D<>("平均", C, Arrays.asList(
                    new ExcelView.D<>("周", d -> TextUtil.price(d.getAvg().getWeek()), C, E, CommonUtil.nullsLast(d -> d.getAvg().getWeek())),
                    new ExcelView.D<>("月", d -> TextUtil.price(d.getAvg().getMonth()), C, E, CommonUtil.nullsLast(d -> d.getAvg().getMonth())),
                    new ExcelView.D<>("季", d -> TextUtil.price(d.getAvg().getSeason()), C, E, CommonUtil.nullsLast(d -> d.getAvg().getSeason())),
                    new ExcelView.D<>("年", d -> TextUtil.price(d.getAvg().getYear()), C, E, CommonUtil.nullsLast(d -> d.getAvg().getYear()))
            ))
    );

    @Override
    protected List<ExcelView.D<Net>> define() {
        return ds;
    }

    @Override
    protected List<Net> listData() {
        String start = this.start.getValue();
        String end = this.end.getValue();
        return this.manager().list(item.getCode(), item.getMarket(), start, end);
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

    protected abstract BaseNetManager<T> manager();

}
