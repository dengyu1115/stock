package com.nature.stock.item.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import com.nature.stock.common.activity.BaseListActivity;
import com.nature.stock.common.manager.WorkdayManager;
import com.nature.stock.common.util.*;
import com.nature.stock.common.view.ExcelView;
import com.nature.stock.common.view.SearchBar;
import com.nature.stock.common.view.Selector;
import com.nature.stock.item.manager.KlineManager;
import com.nature.stock.item.model.Kline;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * K线列表
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:10
 */
public class KlineListActivity extends BaseListActivity<Kline> {

    private final KlineManager klineManager = InstanceHolder.get(KlineManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final List<ExcelView.D<Kline>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Kline::getName), getConsumer()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Kline::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Kline::getDate)),
            new ExcelView.D<>("最新", d -> TextUtil.net(d.getLatest()), C, E, CommonUtil.nullsLast(Kline::getLatest)),
            new ExcelView.D<>("今开", d -> TextUtil.net(d.getOpen()), C, E, CommonUtil.nullsLast(Kline::getOpen)),
            new ExcelView.D<>("最高", d -> TextUtil.net(d.getHigh()), C, E, CommonUtil.nullsLast(Kline::getHigh)),
            new ExcelView.D<>("最低", d -> TextUtil.net(d.getLow()), C, E, CommonUtil.nullsLast(Kline::getLow)),
            new ExcelView.D<>("周平均", d -> TextUtil.net(d.getAvgWeek()), C, E, CommonUtil.nullsLast(Kline::getAvgWeek)),
            new ExcelView.D<>("月平均", d -> TextUtil.net(d.getAvgMonth()), C, E, CommonUtil.nullsLast(Kline::getAvgMonth)),
            new ExcelView.D<>("季平均", d -> TextUtil.net(d.getAvgSeason()), C, E, CommonUtil.nullsLast(Kline::getAvgSeason)),
            new ExcelView.D<>("年平均", d -> TextUtil.net(d.getAvgYear()), C, E, CommonUtil.nullsLast(Kline::getAvgYear))
    );
    private Selector<String> selector;
    private EditText editText;
    private Button reload, loadLatest;

    private Consumer<Kline> getConsumer() {
        return d -> {
            Intent intent = new Intent(getApplicationContext(), KlineActivity.class);
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
        String date = this.selector.getValue();
        String keyWord = this.editText.getText().toString();
        return klineManager.listByDate(date, keyWord);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("重新加载", 80, 30));
        searchBar.addConditionView(loadLatest = template.button("加载最新", 80, 30));
        searchBar.addConditionView(selector = template.selector(80, 30));
        searchBar.addConditionView(editText = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        selector.mapper(s -> s).init().refreshData(workDayManager.listWorkDays(workDayManager.getLatestWorkDay()));
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", klineManager.reloadAll());
                            this.refreshData();
                            return s;
                        })
                )
        );
        loadLatest.setOnClickListener(v ->
                ClickUtil.asyncClick(v, () -> {
                    String s = String.format("加载完成,共%s条", klineManager.loadLatest());
                    this.refreshData();
                    return s;
                }));
    }

}
