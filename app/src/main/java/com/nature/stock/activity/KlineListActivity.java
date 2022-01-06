package com.nature.stock.activity;

import android.content.Intent;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.manager.KlineManager;
import com.nature.stock.model.Kline;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class KlineListActivity extends BaseListActivity<Kline> {

    private final KlineManager klineManager = InstanceHolder.get(KlineManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final List<ExcelView.D<Kline>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Kline::getName), this.detail()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Kline::getCode), this.view()),
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
    private Selector<String> date;
    private EditText keyword;

    @Override
    protected List<ExcelView.D<Kline>> define() {
        return ds;
    }

    @Override
    protected List<Kline> listData() {
        String date = this.date.getValue();
        String keyWord = this.keyword.getText().toString();
        return klineManager.listByDate(date, keyWord);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(date = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        date.mapper(s -> s).init().refreshData(workDayManager.listWorkDays(workDayManager.getLatestWorkDay()));
    }

    private Consumer<Kline> detail() {
        return this.consume(KlineDetailActivity.class);
    }

    private Consumer<Kline> view() {
        return this.consume(KlineViewActivity.class);
    }

    private Consumer<Kline> consume(Class<?> clz) {
        return d -> {
            Intent intent = new Intent(context, clz);
            intent.putExtra("data", JSON.toJSONString(d));
            this.startActivity(intent);
        };
    }

}
