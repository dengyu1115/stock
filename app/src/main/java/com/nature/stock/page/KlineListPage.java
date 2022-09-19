package com.nature.stock.page;

import android.widget.EditText;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.ListPage;
import com.nature.common.page.Page;
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

@PageView(name = "K线-整合", group = "股票", col = 1, row = 5)
public class KlineListPage extends ListPage<Kline> {
    private final List<ExcelView.D<Kline>> ds = Arrays.asList(
            new ExcelView.D<>("", C, Arrays.asList(
                    new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Kline::getName), this.detail()),
                    new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Kline::getCode), this.view()))
            ),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Kline::getDate)),
            new ExcelView.D<>("交易量", d -> TextUtil.amount(d.getShare()), C, E, CommonUtil.nullsLast(Kline::getShare)),
            new ExcelView.D<>("交易额", d -> TextUtil.amount(d.getAmount()), C, E, CommonUtil.nullsLast(Kline::getAmount)),
            new ExcelView.D<>("当前", C, Arrays.asList(
                    new ExcelView.D<>("最新", d -> TextUtil.price(d.getPriceLatest()), C, E, CommonUtil.nullsLast(Kline::getPriceLatest)),
                    new ExcelView.D<>("今开", d -> TextUtil.price(d.getPriceOpen()), C, E, CommonUtil.nullsLast(Kline::getPriceOpen)),
                    new ExcelView.D<>("最高", d -> TextUtil.price(d.getPriceHigh()), C, E, CommonUtil.nullsLast(Kline::getPriceHigh)),
                    new ExcelView.D<>("最低", d -> TextUtil.price(d.getPriceLow()), C, E, CommonUtil.nullsLast(Kline::getPriceLow))
            )),
            new ExcelView.D<>("累计", C, Arrays.asList(
                    new ExcelView.D<>("最新", d -> TextUtil.price(d.getLatest()), C, E, CommonUtil.nullsLast(Kline::getLatest)),
                    new ExcelView.D<>("今开", d -> TextUtil.price(d.getOpen()), C, E, CommonUtil.nullsLast(Kline::getOpen)),
                    new ExcelView.D<>("最高", d -> TextUtil.price(d.getHigh()), C, E, CommonUtil.nullsLast(Kline::getHigh)),
                    new ExcelView.D<>("最低", d -> TextUtil.price(d.getLow()), C, E, CommonUtil.nullsLast(Kline::getLow))
            )),
            new ExcelView.D<>("平均", C, Arrays.asList(
                    new ExcelView.D<>("周", d -> TextUtil.price(d.getAvgWeek()), C, E, CommonUtil.nullsLast(Kline::getAvgWeek)),
                    new ExcelView.D<>("月", d -> TextUtil.price(d.getAvgMonth()), C, E, CommonUtil.nullsLast(Kline::getAvgMonth)),
                    new ExcelView.D<>("季", d -> TextUtil.price(d.getAvgSeason()), C, E, CommonUtil.nullsLast(Kline::getAvgSeason)),
                    new ExcelView.D<>("年", d -> TextUtil.price(d.getAvgYear()), C, E, CommonUtil.nullsLast(Kline::getAvgYear))
            ))
    );
    @Injection
    private KlineManager klineManager;
    @Injection
    private WorkdayManager workDayManager;
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
        return this.consume(KlineDetailPage.class);
    }

    private Consumer<Kline> view() {
        return this.consume(KlineViewPage.class);
    }

    private Consumer<Kline> consume(Class<? extends Page> clz) {
        return d -> {
            this.show(clz, d);
        };
    }

}
