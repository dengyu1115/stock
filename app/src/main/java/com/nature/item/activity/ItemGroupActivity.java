package com.nature.item.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.Selector;
import com.nature.common.view.ViewTemplate;
import com.nature.item.manager.GroupManager;
import com.nature.item.manager.ItemGroupManager;
import com.nature.item.manager.ItemManager;
import com.nature.item.model.Group;
import com.nature.item.model.Item;
import com.nature.item.model.ItemGroup;
import com.nature.stock.activity.KlineViewActivity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;

public class ItemGroupActivity extends AppCompatActivity {

    private static final int CENTER = 0, START = 1;
    private final ItemManager itemManager = InstanceHolder.get(ItemManager.class);
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private final ItemGroupManager itemGroupManager = InstanceHolder.get(ItemGroupManager.class);
    private Context context;
    private LinearLayout page;
    private float uw, uh;
    private EditText tText, aText;
    private Button tQuery, aQuery;
    private ExcelView<Item> toAdd, added;
    private Selector<Group> groupSel;
    private ViewTemplate template;
    private String lkw;
    private String group;
    private final List<ExcelView.D<Item>> lds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), CENTER, START, CommonUtil.nullsLast(Item::getName), toKlineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), CENTER, START, CommonUtil.nullsLast(Item::getCode), toKlineView()),
            new ExcelView.D<>("操作", d -> "+", CENTER, CENTER, this.leftClick())
    );

    private final List<ExcelView.D<Item>> rds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), CENTER, START, CommonUtil.nullsLast(Item::getName), toKlineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), CENTER, START, CommonUtil.nullsLast(Item::getCode)),
            new ExcelView.D<>("操作", d -> "—", CENTER, CENTER, this.rightClick())
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ItemGroupActivity.this;
        this.makeStructure();
        this.initBehaviours();
        this.setContentView(page);
        ViewUtil.initActivity(ItemGroupActivity.this);
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        page = new LinearLayout(context);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        this.uw = metrics.widthPixels / 100f;
        this.uh = metrics.heightPixels / 100f;
        this.header();
        this.body();
    }

    private void initBehaviours() {
        this.groupSel.mapper(Group::getName).onChangeRun(this::refreshRightExcel).init()
                .refreshData(groupManager.list(null));
        this.initSelectedGroup();
        this.toAdd.define(lds);
        this.toAdd.data(itemManager.list(lkw));
        this.added.define(rds);
        this.refreshRightExcel();
        this.tQuery.setOnClickListener(v -> this.refreshLeftExcel());
        this.aQuery.setOnClickListener(v -> this.refreshRightExcel());
    }

    private void initSelectedGroup() {
        Group group = JSON.parseObject(this.getIntent().getStringExtra("group"), Group.class);
        if (group != null) {
            this.groupSel.setValue(group);
        }
    }

    private void header() {
        LinearLayout line = this.block(100, 5);
        page.addView(line);
        line.addView(groupSel = new Selector<>(context));
        LinearLayout layout = this.block(100, 5);
        page.addView(layout);
        LinearLayout left = this.block(50, 5);
        left.addView(tText = template.editText(120, 30));
        left.addView(tQuery = template.button("查询", 60, 30));
        LinearLayout right = this.block(50, 5);
        right.addView(aText = template.editText(120, 30));
        right.addView(aQuery = template.button("查询", 60, 30));
        layout.addView(left);
        layout.addView(right);
    }

    private void body() {
        LinearLayout layout = this.block(100, 90);
        LayoutParams param = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layout.setLayoutParams(param);
        page.addView(layout);
        LayoutParams p = new LayoutParams(width(50), MATCH_PARENT);
        LinearLayout left = this.block(50, 90);
        left.setLayoutParams(p);
        LinearLayout right = this.block(50, 90);
        right.setLayoutParams(p);
        left.addView(toAdd = new ExcelView<>(context, 3, 0.49f));
        right.addView(added = new ExcelView<>(context, 3, 0.49f));
        layout.addView(left);
        layout.addView(divider());
        layout.addView(right);
    }

    private View divider() {
        View view = new View(context);
        LayoutParams param = new LayoutParams(1, -1);
        view.setLayoutParams(param);
        view.setBackgroundColor(Color.LTGRAY);
        return view;
    }

    private LinearLayout block(int width, int height) {
        LinearLayout layout = new LinearLayout(context);
        LayoutParams param = new LayoutParams(width(width), height(height));
        layout.setLayoutParams(param);
        layout.setGravity(Gravity.CENTER);
        return layout;
    }

    private int width(float percent) {
        return (int) (uw * percent + 0.5f);
    }

    private int height(float percent) {
        return (int) (uh * percent + 0.5f);
    }

    private Consumer<Item> leftClick() {
        return i -> {
            ItemGroup ig = new ItemGroup();
            ig.setGroup(this.group);
            ig.setCode(i.getCode());
            ig.setMarket(i.getMarket());
            ig.setName(i.getName());
            ig.setType(i.getType());
            itemGroupManager.merge(ig);
            this.refreshRightExcel();
        };
    }

    private Consumer<Item> rightClick() {
        return i -> {
            itemGroupManager.delete(this.group, i.getCode(), i.getMarket());
            this.refreshRightExcel();
        };
    }

    private Consumer<Item> toKlineView() {
        return d -> {
            Intent intent = new Intent(getApplicationContext(), KlineViewActivity.class);
            intent.putExtra("code", d.getCode());
            intent.putExtra("market", d.getMarket());
            this.startActivity(intent);
        };
    }

    private void refreshLeftExcel() {
        lkw = this.tText.getText().toString();
        this.toAdd.data(itemManager.list(lkw));
    }

    private void refreshRightExcel() {
        group = this.groupSel.getValue().getCode();
        String rkw = this.aText.getText().toString();
        this.added.data(itemGroupManager.listItem(group, rkw));
    }

}
