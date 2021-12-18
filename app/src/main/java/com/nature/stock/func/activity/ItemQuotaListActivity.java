package com.nature.stock.func.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.nature.stock.common.activity.BaseListActivity;
import com.nature.stock.common.enums.ItemType;
import com.nature.stock.func.manager.ItemQuotaManager;
import com.nature.stock.func.model.ItemQuota;
import com.nature.stock.common.util.CommonUtil;
import com.nature.stock.common.util.InstanceHolder;
import com.nature.stock.common.util.Sorter;
import com.nature.stock.common.util.TextUtil;
import com.nature.stock.common.view.ExcelView;
import com.nature.stock.common.view.SearchBar;
import com.nature.stock.common.view.Selector;
import com.nature.stock.item.activity.KlineActivity;
import com.nature.stock.item.manager.GroupManager;
import com.nature.stock.item.model.Group;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * 项目指标
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:09
 */
public class ItemQuotaListActivity extends BaseListActivity<ItemQuota> {

    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private final ItemQuotaManager itemQuotaManager = InstanceHolder.get(ItemQuotaManager.class);
    private Button dateStart, dateEnd;
    private Selector<Group> selector;
    private Group group;
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

    @SuppressLint("ResourceType")
    private void onDateChooseClick(View v) {
        Button button = (Button) v;
        template.datePiker(button);
    }

    protected List<ItemQuota> listData() {
        this.group = selector.getValue();
        String dateStart = this.dateStart.getText().toString();
        String dateEnd = this.dateEnd.getText().toString();
        return itemQuotaManager.list(this.group, this.keyword.getText().toString(), dateStart, dateEnd);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        String end = CommonUtil.formatDate(new Date());
        String start = CommonUtil.addMonths(end, -1);
        searchBar.addConditionView(selector = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
        searchBar.addConditionView(dateStart = template.button(start, 80, 30));
        searchBar.addConditionView(dateEnd = template.button(end, 80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        List<Group> list = groupManager.list(ItemType.INDEX.getCode());
        list.addAll(groupManager.list(ItemType.STOCK.getCode()));
        selector.mapper(Group::getName).init().refreshData(list);
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
            if (ItemType.STOCK.getCode().equals(this.group.getType())) {
                intent = new Intent(context, FundLineActivity.class);
                intent.putExtra("fund", JSON.toJSONString(d));
            } else {
                intent = new Intent(context, KlineActivity.class);
                intent.putExtra("market", d.getMarket());
                intent.putExtra("code", d.getCode());
            }
            this.startActivity(intent);
        };
    }

}
