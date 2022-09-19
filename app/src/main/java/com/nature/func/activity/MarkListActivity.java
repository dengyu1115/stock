package com.nature.func.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.common.view.ViewTemplate;
import com.nature.func.manager.MarkManager;
import com.nature.func.model.Mark;
import com.nature.item.manager.GroupManager;
import com.nature.item.manager.ItemGroupManager;
import com.nature.item.manager.ItemManager;
import com.nature.item.model.Group;
import com.nature.item.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 标记
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:14
 */
public class MarkListActivity extends BaseListActivity<Mark> {

    private final MarkManager markManager = InstanceHolder.get(MarkManager.class);
    private final ItemManager itemManager = InstanceHolder.get(ItemManager.class);
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private final ItemGroupManager itemGroupManager = InstanceHolder.get(ItemGroupManager.class);
    private EditText keyword;
    private LinearLayout window;
    private EditText text, price, rateBuy, rateSell;
    private Button add, date;
    private Selector<String> typeSel;
    private Selector<Group> groupSel;
    private Selector<Item> itemSel;
    private List<Item> items;
    private final List<ExcelView.D<Mark>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Mark::getName), this.detail()),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, C, CommonUtil.nullsLast(Mark::getCode)),
            new ExcelView.D<>("market", d -> TextUtil.text(d.getMarket()), C, C, CommonUtil.nullsLast(Mark::getMarket)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, C, CommonUtil.nullsLast(Mark::getDate)),
            new ExcelView.D<>("价格", d -> TextUtil.net(d.getPrice()), C, E, CommonUtil.nullsLast(Mark::getPrice)),
            new ExcelView.D<>("补仓跌幅", d -> TextUtil.hundred(d.getRateBuy()), C, E, CommonUtil.nullsLast(Mark::getRateBuy)),
            new ExcelView.D<>("补仓价格", d -> TextUtil.net(d.getPriceBuy()), C, E, CommonUtil.nullsLast(Mark::getPriceBuy)),
            new ExcelView.D<>("止盈涨幅", d -> TextUtil.hundred(d.getRateSell()), C, E, CommonUtil.nullsLast(Mark::getRateSell)),
            new ExcelView.D<>("止盈价格", d -> TextUtil.net(d.getPriceSell()), C, E, CommonUtil.nullsLast(Mark::getPriceSell)),
            new ExcelView.D<>("编辑", d -> "+", C, C, this.edit()),
            new ExcelView.D<>("操作", d -> "-", C, C, this.delete())
    );
    private Map<String, String> itemMap;

    @Override
    protected List<ExcelView.D<Mark>> define() {
        return ds;
    }

    @Override
    protected List<Mark> listData() {
        Mark mark = this.getInitMark();
        List<Mark> list;
        if (mark == null) {
            String keyword = this.keyword.getText().toString();
            list = markManager.list();
            Stream<Mark> stream = list.stream().peek(i -> i.setName(itemMap.get(this.key(i.getCode(), i.getMarket()))));
            Group group = this.groupSel.getValue();
            if (group != null && group.getCodes() != null) {
                stream = stream.filter(i -> group.getCodes().contains(i.getCode()));
            }
            if (StringUtils.isNotBlank(keyword)) {
                stream = stream.filter(i -> i.getName().contains(keyword) || i.getCode().contains(keyword));
            }
            list = stream.collect(Collectors.toList());
        } else {
            list = markManager.list(mark.getCode(), mark.getMarket());
            list.forEach(i -> i.setName(itemMap.get(this.key(i.getCode(), i.getMarket()))));
        }
        return list;
    }


    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        if (this.getInitMark() != null) {
            return;
        }
        searchBar.addConditionView(add = template.button("+", 30, 30));
        searchBar.addConditionView(groupSel = template.selector(100, 30));
        searchBar.addConditionView(keyword = template.editText(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        List<Item> items = itemManager.list();
        itemMap = items.stream().collect(Collectors.toMap(i -> this.key(i.getCode(), i.getMarket()), Item::getName));
        if (this.getInitMark() != null) {
            return;
        }
        add.setOnClickListener(v -> this.popAdd());
        groupSel.mapper(Group::getName).init().refreshData(this.listGroups());
        this.initAddMark();
    }

    private void initAddMark() {
        Item item = JSON.parseObject(this.getIntent().getStringExtra("item"), Item.class);
        if (item == null) {
            return;
        }
        this.popAdd();
    }

    private void popAdd() {
        this.makeWindowStructure();
        this.initWindowBehaviours();
        PopUtil.confirm(context, "新增标记", window, this::doSave);
    }

    private Consumer<Mark> edit() {
        return d -> {
            this.makeWindowStructure();
            this.initWindowBehaviours();
            this.itemSel.setValue(d);
            this.date.setText(d.getDate());
            this.price.setText(TextUtil.net(d.getPrice()));
            this.rateBuy.setText(TextUtil.text(d.getRateBuy()));
            this.rateSell.setText(TextUtil.text(d.getRateSell()));
            PopUtil.confirm(context, "编辑标记", window, this::doSave);
        };
    }

    private void doSave() {
        Item item = this.itemSel.getValue();
        if (item == null) {
            throw new RuntimeException("请选择项目");
        }
        String date = this.date.getText().toString();
        if (date.isEmpty()) {
            throw new RuntimeException("请选择日期");
        }
        String price = this.price.getText().toString();
        if (price.isEmpty()) {
            throw new RuntimeException("请填写价格");
        }
        String rateBuy = this.rateBuy.getText().toString();
        if (rateBuy.isEmpty()) {
            throw new RuntimeException("请填写补仓跌幅");
        }
        String rateSell = this.rateSell.getText().toString();
        if (rateSell.isEmpty()) {
            throw new RuntimeException("请填写补仓跌幅");
        }
        Mark mark = new Mark();
        mark.setCode(item.getCode());
        mark.setMarket(item.getMarket());
        mark.setDate(date);
        mark.setPrice(Double.valueOf(price));
        mark.setRateBuy(Double.valueOf(rateBuy));
        mark.setRateSell(Double.valueOf(rateSell));
        markManager.merge(mark);
        this.refreshData();
        PopUtil.alert(context, "保存成功");
    }

    private List<Group> listGroups() {
        List<Group> groups = groupManager.list(null);
        Group group = new Group();
        group.setName("-请选择-");
        groups.add(0, group);
        return groups;
    }

    private Consumer<Mark> detail() {
        return d -> {
            if (this.getInitMark() == null) {
                Intent intent = new Intent(context, MarkListActivity.class);
                intent.putExtra("mark", JSON.toJSONString(d));
                this.startActivity(intent);
            }
        };
    }

    private Consumer<Mark> delete() {
        return d -> PopUtil.confirm(context, "删除数据", "确认删除吗？", () -> {
            markManager.delete(d.getCode(), d.getMarket(), d.getDate());
            this.refreshData();
            PopUtil.alert(context, "删除成功");
        });
    }

    private String key(String code, String market) {
        return String.join(":", code, market);
    }

    private Mark getInitMark() {
        return JSON.parseObject(this.getIntent().getStringExtra("mark"), Mark.class);
    }

    private void initWindowBehaviours() {
        itemSel.mapper(Item::getName).onChangeRun(this.change()).init();
        Runnable run = () -> {
            List<Item> items = this.items = itemGroupManager.listItem(typeSel.getValue());
            String keyword = this.text.getText().toString();
            List<Item> li = items.stream().filter(i -> i.getName().contains(keyword)).collect(Collectors.toList());
            itemSel.refreshData(li);
        };
        typeSel.mapper(ItemType::codeToName).onChangeRun(run).init().refreshData(ItemType.codes());
        this.initSelected();
        date.setOnClickListener(v -> template.datePiker(date));
        text.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                List<Item> li = items.stream().filter(i -> i.getName().contains(keyword)).collect(Collectors.toList());
                itemSel.refreshData(li);
            }
        });

    }

    private void initSelected() {
        Intent intent = this.getIntent();
        String type = intent.getStringExtra("type");
        Item item = JSON.parseObject(intent.getStringExtra("item"), Item.class);
        if (item == null) {
            return;
        }
        this.typeSel.setValue(type);
        this.itemSel.setValue(item);
        this.recommend();
    }


    private Runnable change() {
        return this::recommend;
    }

    @SuppressLint("DefaultLocale")
    private void recommend() {
        Item item = itemSel.getValue();
        Mark mark = markManager.recommend(item);
        if (mark == null) {
            this.date.setText("");
            this.price.setText("");
            this.rateBuy.setText("");
            this.rateSell.setText("");
        } else {
            this.date.setText(mark.getDate());
            this.price.setText(String.valueOf(mark.getPrice()));
            this.rateBuy.setText(String.format("%.4f", mark.getRateBuy()));
            this.rateSell.setText(String.format("%.4f", mark.getRateSell()));
        }
    }

    private void makeWindowStructure() {
        template = ViewTemplate.build(context);
        window = template.linearPage();
        window.setGravity(Gravity.CENTER);
        LinearLayout l1 = template.line(300, 30);
        LinearLayout l2 = template.line(300, 30);
        LinearLayout l3 = template.line(300, 30);
        LinearLayout l4 = template.line(300, 30);
        LinearLayout l5 = template.line(300, 30);
        LinearLayout l6 = template.line(300, 30);
        l1.addView(template.textView("类型：", 80, 30));
        l1.addView(typeSel = template.selector(100, 30));
        l1.addView(text = template.editText(100, 30));
        l2.addView(template.textView("项目：", 80, 30));
        l2.addView(itemSel = template.selector(200, 30));
        l3.addView(template.textView("日期：", 80, 30));
        l3.addView(date = template.button(200, 30));
        l4.addView(template.textView("价格：", 80, 30));
        l4.addView(price = template.numeric(200, 30));
        l5.addView(template.textView("补仓跌幅：", 80, 30));
        l5.addView(rateBuy = template.numeric(200, 30));
        l6.addView(template.textView("止盈涨幅：", 80, 30));
        l6.addView(rateSell = template.numeric(200, 30));
        window.addView(l1);
        window.addView(l2);
        window.addView(l3);
        window.addView(l4);
        window.addView(l5);
        window.addView(l6);
    }

}
