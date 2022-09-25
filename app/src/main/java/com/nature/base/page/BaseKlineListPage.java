package com.nature.base.page;

import android.widget.EditText;
import com.nature.base.manager.BaseKlineManager;
import com.nature.base.model.Kline;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.page.ListPage;
import com.nature.common.page.Page;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.page.KlineDetailPage;
import com.nature.stock.page.KlineViewPage;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseKlineListPage extends ListPage<Kline> {
    private final List<ExcelView.D<Kline>> ds = Arrays.asList(
            new ExcelView.D<>("", C, Arrays.asList(
                    new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Kline::getName), this.detail()),
                    new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Kline::getCode), this.view()))
            ),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Kline::getDate)),
            new ExcelView.D<>("交易量", d -> TextUtil.amount(d.getShare()), C, E, CommonUtil.nullsLast(Kline::getShare)),
            new ExcelView.D<>("交易额", d -> TextUtil.amount(d.getAmount()), C, E, CommonUtil.nullsLast(Kline::getAmount)),
            new ExcelView.D<>("当前", C, Arrays.asList(
                    new ExcelView.D<>("最新", d -> TextUtil.price(d.getPrice().getLatest()), C, E, CommonUtil.nullsLast(d -> d.getPrice().getLatest())),
                    new ExcelView.D<>("今开", d -> TextUtil.price(d.getPrice().getOpen()), C, E, CommonUtil.nullsLast(d -> d.getPrice().getOpen())),
                    new ExcelView.D<>("最高", d -> TextUtil.price(d.getPrice().getHigh()), C, E, CommonUtil.nullsLast(d -> d.getPrice().getHigh())),
                    new ExcelView.D<>("最低", d -> TextUtil.price(d.getPrice().getLow()), C, E, CommonUtil.nullsLast(d -> d.getPrice().getLow()))
            )),
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
        return this.manager().listByDate(date, keyWord);
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

    protected abstract BaseKlineManager manager();

}
