package com.nature.stock.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.manager.NetManager;
import com.nature.stock.model.Net;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class NetListPage extends ListPage<Net> {

    private final NetManager netManager = InstanceHolder.get(NetManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final List<ExcelView.D<Net>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Net::getName), this.detail()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Net::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Net::getDate)),
            new ExcelView.D<>("最新", d -> TextUtil.price(d.getLatest()), C, E, CommonUtil.nullsLast(Net::getLatest)),
            new ExcelView.D<>("今开", d -> TextUtil.price(d.getOpen()), C, E, CommonUtil.nullsLast(Net::getOpen)),
            new ExcelView.D<>("最高", d -> TextUtil.price(d.getHigh()), C, E, CommonUtil.nullsLast(Net::getHigh)),
            new ExcelView.D<>("最低", d -> TextUtil.price(d.getLow()), C, E, CommonUtil.nullsLast(Net::getLow)),
            new ExcelView.D<>("平均-周", d -> TextUtil.price(d.getAvgWeek()), C, E, CommonUtil.nullsLast(Net::getAvgWeek)),
            new ExcelView.D<>("平均-月", d -> TextUtil.price(d.getAvgMonth()), C, E, CommonUtil.nullsLast(Net::getAvgMonth)),
            new ExcelView.D<>("平均-季", d -> TextUtil.price(d.getAvgSeason()), C, E, CommonUtil.nullsLast(Net::getAvgSeason)),
            new ExcelView.D<>("平均-年", d -> TextUtil.price(d.getAvgYear()), C, E, CommonUtil.nullsLast(Net::getAvgYear))
    );
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
        return netManager.listByDate(date, keyWord);
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
                            String s = String.format("加载完成,共%s条", netManager.reload());
                            this.refreshData();
                            return s;
                        })));
        load.setOnClickListener(v ->
                ClickUtil.asyncClick(v, () -> {
                    String s = String.format("加载完成,共%s条", netManager.load());
                    this.refreshData();
                    return s;
                }));
    }

    private Consumer<Net> detail() {
        return d -> {
            this.show(NetDetailPage.class, d);
        };
    }

}
