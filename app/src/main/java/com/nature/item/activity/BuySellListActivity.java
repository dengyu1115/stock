package com.nature.item.activity;

import android.content.Intent;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.item.manager.KlineManager;
import com.nature.item.model.Kline;
import com.nature.stock.page.KlineViewPage;

import java.util.*;
import java.util.function.Consumer;

/**
 * 买入卖出建议
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 18:55
 */
public class BuySellListActivity extends BaseListActivity<Kline> {

    private static final Map<String, String> typeMap = new LinkedHashMap<>();

    static {
        typeMap.put("1", "可购买");
        typeMap.put("2", "可卖出");
    }

    private final KlineManager klineManager = InstanceHolder.get(KlineManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final List<ExcelView.D<Kline>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Kline::getName), getConsumer()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Kline::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Kline::getDate)),
            new ExcelView.D<>("最新", d -> TextUtil.net(d.getLatest()), C, S, CommonUtil.nullsLast(Kline::getLatest)),
            new ExcelView.D<>("今开", d -> TextUtil.net(d.getOpen()), C, S, CommonUtil.nullsLast(Kline::getOpen)),
            new ExcelView.D<>("最高", d -> TextUtil.net(d.getHigh()), C, S, CommonUtil.nullsLast(Kline::getHigh)),
            new ExcelView.D<>("最低", d -> TextUtil.net(d.getLow()), C, S, CommonUtil.nullsLast(Kline::getLow)),
            new ExcelView.D<>("周平均", d -> TextUtil.net(d.getAvgWeek()), C, S, CommonUtil.nullsLast(Kline::getAvgWeek)),
            new ExcelView.D<>("月平均", d -> TextUtil.net(d.getAvgMonth()), C, S, CommonUtil.nullsLast(Kline::getAvgMonth)),
            new ExcelView.D<>("季平均", d -> TextUtil.net(d.getAvgSeason()), C, S, CommonUtil.nullsLast(Kline::getAvgSeason)),
            new ExcelView.D<>("年平均", d -> TextUtil.net(d.getAvgYear()), C, S, CommonUtil.nullsLast(Kline::getAvgYear))
    );
    private Selector<String> dateSel, typeSel;

    private Consumer<Kline> getConsumer() {
        return d -> {
            Intent intent = new Intent(getApplicationContext(), KlineViewPage.class);
            intent.putExtra("code", d.getCode());
            intent.putExtra("market", d.getMarket());
            this.startActivity(intent);
        };
    }

    @Override
    protected List<ExcelView.D<Kline>> define() {
        return ds;
    }

    @Override
    protected List<Kline> listData() {
        String date = this.dateSel.getValue();
        String type = this.typeSel.getValue();
        return klineManager.listByStrategy(date, type);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(dateSel = template.selector(100, 30));
        searchBar.addConditionView(typeSel = template.selector(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        dateSel.mapper(s -> s).init().refreshData(workDayManager.listWorkDays(workDayManager.getLatestWorkDay()));
        typeSel.mapper(typeMap::get).init().refreshData(new ArrayList<>(typeMap.keySet()));
    }
}
