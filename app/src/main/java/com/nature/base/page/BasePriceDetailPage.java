package com.nature.base.page;

import com.nature.base.manager.BasePriceManager;
import com.nature.base.model.Item;
import com.nature.base.model.Price;
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

public abstract class BasePriceDetailPage<T extends Item> extends ListPage<Price> {

    @Injection
    private WorkdayManager workDayManager;
    private Selector<String> start, end;
    private Item item;
    private final List<ExcelView.D<Price>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(this.getName()), C, S, CommonUtil.nullsLast(Price::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Price::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Price::getDate)),
            new ExcelView.D<>("最新", d -> TextUtil.price(d.getVal().getLatest()), C, E, CommonUtil.nullsLast(d -> d.getVal().getLatest())),
            new ExcelView.D<>("今开", d -> TextUtil.price(d.getVal().getOpen()), C, E, CommonUtil.nullsLast(d -> d.getVal().getOpen())),
            new ExcelView.D<>("最高", d -> TextUtil.price(d.getVal().getHigh()), C, E, CommonUtil.nullsLast(d -> d.getVal().getHigh())),
            new ExcelView.D<>("最低", d -> TextUtil.price(d.getVal().getLow()), C, E, CommonUtil.nullsLast(d -> d.getVal().getLow())),
            new ExcelView.D<>("交易量", d -> TextUtil.amount(d.getShare()), C, E, CommonUtil.nullsLast(Price::getShare)),
            new ExcelView.D<>("交易额", d -> TextUtil.amount(d.getAmount()), C, E, CommonUtil.nullsLast(Price::getAmount))
    );

    @Override
    protected List<ExcelView.D<Price>> define() {
        return ds;
    }

    @Override
    protected List<Price> listData() {
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

    protected abstract BasePriceManager<T> manager();

}
