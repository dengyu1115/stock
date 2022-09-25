package com.nature.base.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.base.manager.BasePriceManager;
import com.nature.base.model.Item;
import com.nature.base.model.Price;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class BasePriceListPage<T extends Item> extends ListPage<Price> {

    private final List<ExcelView.D<Price>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Price::getName), this.detail()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Price::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Price::getDate)),
            new ExcelView.D<>("最新", d -> TextUtil.price(d.getVal().getLatest()), C, E, CommonUtil.nullsLast(d -> d.getVal().getLatest())),
            new ExcelView.D<>("今开", d -> TextUtil.price(d.getVal().getOpen()), C, E, CommonUtil.nullsLast(d -> d.getVal().getOpen())),
            new ExcelView.D<>("最高", d -> TextUtil.price(d.getVal().getHigh()), C, E, CommonUtil.nullsLast(d -> d.getVal().getHigh())),
            new ExcelView.D<>("最低", d -> TextUtil.price(d.getVal().getLow()), C, E, CommonUtil.nullsLast(d -> d.getVal().getLow())),
            new ExcelView.D<>("交易量", d -> TextUtil.amount(d.getShare()), C, E, CommonUtil.nullsLast(Price::getShare)),
            new ExcelView.D<>("交易额", d -> TextUtil.amount(d.getAmount()), C, E, CommonUtil.nullsLast(Price::getAmount))
    );
    @Injection
    private WorkdayManager workDayManager;
    private Selector<String> date;
    private EditText keyword;
    private Button reload, load;

    @Override
    protected List<ExcelView.D<Price>> define() {
        return ds;
    }

    @Override
    protected List<Price> listData() {
        String date = this.date.getValue();
        String keyWord = this.keyword.getText().toString();
        return this.manager().listByDate(date, keyWord);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("重新加载", 80, 30));
        searchBar.addConditionView(load = template.button("加载最新", 80, 30));
        searchBar.addConditionView(date = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        date.mapper(s -> s).init().refreshData(workDayManager.listWorkDays(workDayManager.getLatestWorkDay()));
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", this.manager().reload());
                            this.refreshData();
                            return s;
                        })));
        load.setOnClickListener(v ->
                ClickUtil.asyncClick(v, () -> {
                    String s = String.format("加载完成,共%s条", this.manager().load());
                    this.refreshData();
                    return s;
                }));
    }

    private Consumer<Price> detail() {
        return d -> {
            this.show(this.jumpPage(), d);
        };
    }

    protected abstract BasePriceManager<T> manager();

    protected abstract Class<? extends BasePriceDetailPage<T>> jumpPage();

}
