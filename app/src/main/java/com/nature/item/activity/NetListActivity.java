package com.nature.item.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.activity.FundLineActivity;
import com.nature.func.manager.WorkdayManager;
import com.nature.item.manager.NetManager;
import com.nature.item.model.Net;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * K线列表
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:10
 */
public class NetListActivity extends BaseListActivity<Net> {

    private final NetManager netManager = InstanceHolder.get(NetManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final List<ExcelView.D<Net>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Net::getName), getConsumer()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Net::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, S, CommonUtil.nullsLast(Net::getDate)),
            new ExcelView.D<>("最新净值", d -> TextUtil.net(d.getNet()), C, E, CommonUtil.nullsLast(Net::getNet)),
            new ExcelView.D<>("累计净值", d -> TextUtil.net(d.getNetTotal()), C, E, CommonUtil.nullsLast(Net::getNetTotal)),
            new ExcelView.D<>("增长率", d -> TextUtil.hundred(d.getRate()), C, E, CommonUtil.nullsLast(Net::getRate)),
            new ExcelView.D<>("总增长", d -> TextUtil.hundred(d.getRateTotal()), C, E, CommonUtil.nullsLast(Net::getRateTotal))
    );
    private Selector<String> selector;
    private EditText editText;
    private Button reload, loadLatest;

    private Consumer<Net> getConsumer() {
        return d -> {
            Intent intent = new Intent(context, FundLineActivity.class);
            intent.putExtra("fund", JSON.toJSONString(d));
            this.startActivity(intent);
        };
    }

    @Override
    protected List<ExcelView.D<Net>> define() {
        return ds;
    }

    @Override
    protected List<Net> listData() {
        String date = this.selector.getValue();
        String keyWord = this.editText.getText().toString();
        return netManager.listByDate(date, keyWord);
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
                        () -> {
                            ClickUtil.asyncClick(v, () -> String.format("加载完成,共%s条", netManager.reloadAll()));
                            this.refreshData();
                        }
                )
        );
        loadLatest.setOnClickListener(v ->
                ClickUtil.asyncClick(v, () -> {
                    String s = String.format("加载完成,共%s条", netManager.loadLatest());
                    this.refreshData();
                    return s;
                }));
    }

}
