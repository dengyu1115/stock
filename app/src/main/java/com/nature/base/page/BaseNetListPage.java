package com.nature.base.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.base.manager.BaseNetManager;
import com.nature.base.model.Item;
import com.nature.base.model.Net;
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

public abstract class BaseNetListPage<T extends Item> extends ListPage<Net> {

    private final List<ExcelView.D<Net>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Net::getName), this.detail()),
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
    @Injection
    private WorkdayManager workDayManager;
    private Selector<String> date;
    private EditText keyword;
    private Button reload, load;

    @Override
    protected List<ExcelView.D<Net>> define() {
        return ds;
    }

    @Override
    protected List<Net> listData() {
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
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？", () ->
                        ClickUtil.asyncClick(v, () -> {
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

    private Consumer<Net> detail() {
        return d -> {
            this.show(this.jumpPage(), d);
        };
    }

    protected abstract BaseNetManager<T> manager();

    protected abstract Class<? extends BaseNetDetailPage<T>> jumpPage();

}
