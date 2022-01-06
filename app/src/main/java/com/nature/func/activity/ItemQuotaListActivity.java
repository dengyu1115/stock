package com.nature.func.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.ItemQuotaManager;
import com.nature.func.model.ItemQuota;
import com.nature.item.manager.GroupManager;
import com.nature.item.model.Group;
import com.nature.stock.activity.KlineViewActivity;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemQuotaListActivity extends BaseListActivity<ItemQuota> {

    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private final ItemQuotaManager itemQuotaManager = InstanceHolder.get(ItemQuotaManager.class);
    private Button dateStart, dateEnd;
    private Selector<String> type, group;
    private final List<ExcelView.D<ItemQuota>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(ItemQuota::getName), this.lineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(ItemQuota::getCode)),
            new ExcelView.D<>("开始日期", d -> TextUtil.text(d.getDateStart()), C, E, Sorter.nullsLast(ItemQuota::getDateStart)),
            new ExcelView.D<>("结束日期", d -> TextUtil.text(d.getDateEnd()), C, E, Sorter.nullsLast(ItemQuota::getDateEnd)),
            new ExcelView.D<>("最新", d -> TextUtil.net(d.getLatest()), C, E, Sorter.nullsLast(ItemQuota::getLatest)),
            new ExcelView.D<>("初始", d -> TextUtil.net(d.getOpen()), C, E, Sorter.nullsLast(ItemQuota::getOpen)),
            new ExcelView.D<>("最低", d -> TextUtil.net(d.getLow()), C, E, Sorter.nullsLast(ItemQuota::getLow)),
            new ExcelView.D<>("最高", d -> TextUtil.net(d.getHigh()), C, E, Sorter.nullsLast(ItemQuota::getHigh)),
            new ExcelView.D<>("平均", d -> TextUtil.net(d.getAvg()), C, E, Sorter.nullsLast(ItemQuota::getAvg)),
            new ExcelView.D<>("⬆-初始", d -> TextUtil.hundred(d.getRateOpen()), C, E, Sorter.nullsLast(ItemQuota::getRateOpen)),
            new ExcelView.D<>("⬆-最低", d -> TextUtil.hundred(d.getRateLow()), C, E, Sorter.nullsLast(ItemQuota::getRateLow)),
            new ExcelView.D<>("⬆-最高", d -> TextUtil.hundred(d.getRateHigh()), C, E, Sorter.nullsLast(ItemQuota::getRateHigh)),
            new ExcelView.D<>("⬆-平均", d -> TextUtil.hundred(d.getRateAvg()), C, E, Sorter.nullsLast(ItemQuota::getRateAvg)),
            new ExcelView.D<>("⬆-高-低", d -> TextUtil.hundred(d.getRateHL()), C, E, Sorter.nullsLast(ItemQuota::getRateHL)),
            new ExcelView.D<>("⬆-低-高", d -> TextUtil.hundred(d.getRateLH()), C, E, Sorter.nullsLast(ItemQuota::getRateLH)),
            new ExcelView.D<>("%-平均", d -> TextUtil.hundred(d.getRatioAvg()), C, E, Sorter.nullsLast(ItemQuota::getRatioAvg)),
            new ExcelView.D<>("%-最新", d -> TextUtil.hundred(d.getRatioLatest()), C, E, Sorter.nullsLast(ItemQuota::getRatioLatest))
    );
    private EditText keyword;

    private Map<String, String> groupToName;

    @SuppressLint("ResourceType")
    private void onDateChooseClick(View v) {
        Button button = (Button) v;
        template.datePiker(button);
    }

    protected List<ItemQuota> listData() {
        String dateStart = this.dateStart.getText().toString();
        String dateEnd = this.dateEnd.getText().toString();
        String group = this.group.getValue();
        if (group == null) {
            return new ArrayList<>();
        }
        return itemQuotaManager.list(group, this.keyword.getText().toString(), dateStart, dateEnd);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        String end = CommonUtil.formatDate(new Date());
        String start = CommonUtil.addMonths(end, -1);
        searchBar.addConditionView(type = template.selector(80, 30));
        searchBar.addConditionView(group = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
        searchBar.addConditionView(dateStart = template.button(start, 80, 30));
        searchBar.addConditionView(dateEnd = template.button(end, 80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        group.mapper(this::getGroupName).init();
        type.mapper(ItemType::codeToName).onChangeRun(() -> {
            List<Group> list = groupManager.list(this.type.getValue());
            groupToName = list.stream().collect(Collectors.toMap(Group::getCode, Group::getName));
            List<String> groups = list.stream().map(Group::getCode).collect(Collectors.toList());
            group.refreshData(groups);
            if (!groups.isEmpty()) {
                group.setValue(groups.get(0));
            } else {
                group.setValue(null);
            }
        }).init().refreshData(ItemType.codes());
        Button.OnClickListener listener = this::onDateChooseClick;
        dateStart.setOnClickListener(listener);
        dateEnd.setOnClickListener(listener);
    }

    @Override
    protected List<ExcelView.D<ItemQuota>> define() {
        return ds;
    }

    private Consumer<ItemQuota> lineView() {
        return d -> {
            Intent intent;
            if (ItemType.FUND.getCode().equals(this.group.getValue())) {
                intent = new Intent(context, FundLineActivity.class);
                intent.putExtra("fund", JSON.toJSONString(d));
            } else {
                intent = new Intent(context, KlineViewActivity.class);
                intent.putExtra("market", d.getMarket());
                intent.putExtra("code", d.getCode());
            }
            this.startActivity(intent);
        };
    }

    private String getGroupName(String code) {
        return groupToName.get(code);
    }

}
