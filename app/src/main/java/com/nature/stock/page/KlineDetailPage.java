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
import com.nature.stock.manager.KlineManager;
import com.nature.stock.model.Item;
import com.nature.stock.model.Kline;

import java.util.Arrays;
import java.util.List;

@PageView(name = "K线明细", group = "股票", col = 0, row = 0)
public class KlineDetailPage extends ListPage<Kline> {
    @Injection
    private KlineManager klineManager;
    @Injection
    private WorkdayManager workDayManager;
    private final List<ExcelView.D<Kline>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(this.getName()), C, S, CommonUtil.nullsLast(Kline::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Kline::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Kline::getDate)),
            new ExcelView.D<>("交易量", d -> TextUtil.amount(d.getShare()), C, E, CommonUtil.nullsLast(Kline::getShare)),
            new ExcelView.D<>("交易额", d -> TextUtil.amount(d.getAmount()), C, E, CommonUtil.nullsLast(Kline::getAmount)),
            new ExcelView.D<>("最新", d -> TextUtil.price(d.getPriceLatest()), C, E, CommonUtil.nullsLast(Kline::getPriceLatest)),
            new ExcelView.D<>("今开", d -> TextUtil.price(d.getPriceOpen()), C, E, CommonUtil.nullsLast(Kline::getPriceOpen)),
            new ExcelView.D<>("最高", d -> TextUtil.price(d.getPriceHigh()), C, E, CommonUtil.nullsLast(Kline::getPriceHigh)),
            new ExcelView.D<>("最低", d -> TextUtil.price(d.getPriceLow()), C, E, CommonUtil.nullsLast(Kline::getPriceLow)),
            new ExcelView.D<>("最新-累计", d -> TextUtil.price(d.getLatest()), C, E, CommonUtil.nullsLast(Kline::getLatest)),
            new ExcelView.D<>("今开-累计", d -> TextUtil.price(d.getOpen()), C, E, CommonUtil.nullsLast(Kline::getOpen)),
            new ExcelView.D<>("最高-累计", d -> TextUtil.price(d.getHigh()), C, E, CommonUtil.nullsLast(Kline::getHigh)),
            new ExcelView.D<>("最低-累计", d -> TextUtil.price(d.getLow()), C, E, CommonUtil.nullsLast(Kline::getLow)),
            new ExcelView.D<>("平均-周", d -> TextUtil.price(d.getAvgWeek()), C, E, CommonUtil.nullsLast(Kline::getAvgWeek)),
            new ExcelView.D<>("平均-月", d -> TextUtil.price(d.getAvgMonth()), C, E, CommonUtil.nullsLast(Kline::getAvgMonth)),
            new ExcelView.D<>("平均-季", d -> TextUtil.price(d.getAvgSeason()), C, E, CommonUtil.nullsLast(Kline::getAvgSeason)),
            new ExcelView.D<>("平均-年", d -> TextUtil.price(d.getAvgYear()), C, E, CommonUtil.nullsLast(Kline::getAvgYear))
    );
    private Selector<String> start, end;
    private Item item;

    @Override
    protected List<ExcelView.D<Kline>> define() {
        return ds;
    }

    @Override
    protected List<Kline> listData() {
        String start = this.start.getValue();
        String end = this.end.getValue();
        return klineManager.list(item.getCode(), item.getMarket(), start, end);
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
