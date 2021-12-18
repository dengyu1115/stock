package com.nature.stock.func.activity;

import android.widget.EditText;
import com.nature.stock.common.activity.BaseListActivity;
import com.nature.stock.common.manager.WorkdayManager;
import com.nature.stock.common.util.CommonUtil;
import com.nature.stock.common.util.InstanceHolder;
import com.nature.stock.common.util.TextUtil;
import com.nature.stock.common.view.ExcelView;
import com.nature.stock.common.view.SearchBar;
import com.nature.stock.common.view.Selector;
import com.nature.stock.func.manager.TargetManager;
import com.nature.stock.func.model.Mark;
import com.nature.stock.func.model.Target;
import com.nature.stock.item.manager.GroupManager;
import com.nature.stock.item.manager.ItemManager;
import com.nature.stock.item.model.Group;
import com.nature.stock.item.model.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TargetListActivity extends BaseListActivity<Target> {

    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("0", "涨幅");
        map.put("1", "买入");
        map.put("2", "卖出");
    }

    private final TargetManager targetManager = InstanceHolder.get(TargetManager.class);
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final ItemManager itemManager = InstanceHolder.get(ItemManager.class);
    private EditText keyword;
    private Selector<String> typeSel;
    private Selector<Group> groupSel;
    private Selector<String> dateSel;
    private Map<String, String> itemMap;

    private String type;

    private final List<ExcelView.D<Target>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Target::getName)),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, C, CommonUtil.nullsLast(Target::getCode)),
            new ExcelView.D<>("market", d -> TextUtil.text(d.getMarket()), C, C, CommonUtil.nullsLast(Target::getMarket)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, C, CommonUtil.nullsLast(Target::getDate)),
            new ExcelView.D<>("价格", d -> TextUtil.net(d.getPrice()), C, C, CommonUtil.nullsLast(Target::getPrice)),
            new ExcelView.D<>("日期-标记", d -> TextUtil.text(this.getMarkDate(d)), C, C, CommonUtil.nullsLast(this::getMarkDate)),
            new ExcelView.D<>("价格-标记", d -> TextUtil.net(this.getMarkPrice(d)), C, C, CommonUtil.nullsLast(this::getMarkPrice)),
            new ExcelView.D<>("价格-目标", d -> TextUtil.net(this.getTargetPrice(d)), C, C, CommonUtil.nullsLast(this::getTargetPrice)),
            new ExcelView.D<>("涨跌幅", d -> TextUtil.hundred(d.getRate()), C, C, CommonUtil.nullsLast(Target::getRate)),
            new ExcelView.D<>("涨跌幅-目标", d -> TextUtil.hundred(this.getTargetRate(d)), C, C, CommonUtil.nullsLast(this::getTargetRate)),
            new ExcelView.D<>("操作", d -> TextUtil.text(this.getHandleText()), C, C, this.handle())
    );

    @Override
    protected List<ExcelView.D<Target>> define() {
        return ds;
    }

    @Override
    protected List<Target> listData() {
        type = this.typeSel.getValue();
        Group group = this.groupSel.getValue();
        String date = this.dateSel.getValue();
        String keyword = this.keyword.getText().toString();
        Stream<Target> stream = targetManager.list(type, date).stream();
        if (group != null && group.getCodes() != null) {
            stream = stream.filter(i -> group.getCodes().contains(i.getCode()));
        }
        stream = stream.peek(i -> i.setName(itemMap.get(this.key(i.getCode(), i.getMarket()))));
        if (!keyword.isEmpty()) {
            stream = stream.filter(i -> i.getName().contains(keyword));
        }
        return stream.collect(Collectors.toList());
    }


    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(typeSel = template.selector(80, 30));
        searchBar.addConditionView(groupSel = template.selector(80, 30));
        searchBar.addConditionView(dateSel = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        List<Item> items = itemManager.list();
        itemMap = items.stream().collect(Collectors.toMap(i -> this.key(i.getCode(), i.getMarket()), Item::getName));
        typeSel.mapper(map::get).init().refreshData(Arrays.asList("0", "1", "2"));
        groupSel.mapper(Group::getName).init().refreshData(this.getGroups());
        dateSel.mapper(i -> i).init().refreshData(workDayManager.listWorkDays(workDayManager.getLatestWorkDay()));
    }

    private String key(String code, String market) {
        return String.join(":", code, market);
    }

    private Double getTargetRate(Target d) {
        Mark mark = d.getMark();
        return "1".equals(type) ? mark.getRateBuy() : mark.getRateSell();
    }

    private List<Group> getGroups() {
        List<Group> list = groupManager.list(null);
        list = list.stream().filter(i -> i.getCodes() != null && !i.getCodes().isEmpty()).collect(Collectors.toList());
        Group group = new Group();
        group.setName("--请选择--");
        list.add(0, group);
        return list;
    }

    private Double getMarkPrice(Target d) {
        return d.getMark().getPrice();
    }

    private String getMarkDate(Target d) {
        return d.getMark().getDate();
    }

    private Double getTargetPrice(Target d) {
        Mark mark = d.getMark();
        return mark.getPrice() * (1 + ("1".equals(type) ? mark.getRateBuy() : mark.getRateSell()));
    }

    private String getHandleText() {
        String type = this.typeSel.getValue();
        if ("0".equals(type)) {
            return null;
        } else if ("1".equals(type)) {
            return "+";
        } else if ("2".equals(type)) {
            return "-";
        } else {
            return null;
        }
    }

    private Consumer<Target> handle() {
        return null;
    }
}
