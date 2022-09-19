package com.nature.base.page;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.nature.base.manager.BaseItemGroupManager;
import com.nature.base.manager.BaseItemManager;
import com.nature.base.model.Item;
import com.nature.base.model.ItemGroup;
import com.nature.common.page.Page;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.ViewTemplate;
import com.nature.stock.page.KlineViewPage;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseItemGroupPage<T extends Item> extends Page {

    private static final int CENTER = 0, START = 1;
    private Context context;
    private LinearLayout page;
    private float uw, uh;
    private EditText tText, aText;
    private Button tQuery, aQuery;
    private ExcelView<T> toAdd, added;
    protected ViewTemplate template;
    protected String keyword;
    private String group;
    private List<T> toAddList, addedList;
    private final List<ExcelView.D<T>> lds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), CENTER, START, CommonUtil.nullsLast(T::getName), this.toKlineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), CENTER, START, CommonUtil.nullsLast(T::getCode), this.toKlineView()),
            new ExcelView.D<>("操作", d -> "+", CENTER, CENTER, this.leftClick())
    );

    private final List<ExcelView.D<T>> rds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), CENTER, START, CommonUtil.nullsLast(T::getName), this.toKlineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), CENTER, START, CommonUtil.nullsLast(T::getCode)),
            new ExcelView.D<>("操作", d -> "—", CENTER, CENTER, this.rightClick())
    );


    @Override
    protected void makeStructure(LinearLayout page, Context context) {
        this.page = page;
        this.context = context;
        this.makeStructure();
    }

    @Override
    protected void onShow() {
        this.initBehaviours();
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        this.uw = 2140 / 100f;
        this.uh = metrics.heightPixels / 100f;
        this.header();
        this.body();
    }

    private void initBehaviours() {
        this.tText.setText("");
        this.aText.setText("");
        this.extraBehaviours();
        this.toAddList = this.itemManager().list();
        if (StringUtils.isBlank(this.group = this.getParam())) {
            this.addedList = new ArrayList<>();
        } else {
            this.addedList = this.listAdded();
        }
        if (!this.addedList.isEmpty()) {
            this.toAddList.removeAll(new HashSet<>(this.addedList));
        }
        this.tQuery.setOnClickListener(v -> this.refreshLeftExcel());
        this.aQuery.setOnClickListener(v -> this.refreshRightExcel());
        List<ExcelView.D<T>> rightDs = new ArrayList<>(rds);
        this.extraColumns(rightDs);
        this.added.define(rightDs);
        List<ExcelView.D<T>> leftDs = new ArrayList<>(lds);
        this.extraColumns(leftDs);
        this.toAdd.define(leftDs);
        this.toAdd.data(this.toAddList);
        this.added.data(this.addedList);
    }

    private List<T> listAdded() {
        List<ItemGroup> itemGroups = this.manager().listByGroup(this.group);
        Set<String> set = itemGroups.stream().map(this::key).collect(Collectors.toSet());
        return this.toAddList.stream().filter(i -> set.contains(this.key(i))).collect(Collectors.toList());
    }

    private void header() {
        LinearLayout layout = this.block(100, 10);
        page.addView(layout);
        LinearLayout left = this.block(50, 10);
        left.addView(tText = template.editText(100, 30));
        this.extraViews(left);
        left.addView(tQuery = template.button("查询", 60, 30));
        LinearLayout right = this.block(50, 10);
        right.addView(aText = template.editText(100, 30));
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
        left.addView(toAdd = new ExcelView<>(context, 5, 0.49f));
        right.addView(added = new ExcelView<>(context, 5, 0.49f));
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

    private Consumer<T> leftClick() {
        return i -> {
            ItemGroup ig = new ItemGroup();
            ig.setGroup(this.group);
            ig.setCode(i.getCode());
            ig.setMarket(i.getMarket());
            this.manager().merge(ig);
            addedList.add(i);
            toAddList.remove(i);
            this.refreshLeftExcel();
            this.refreshRightExcel();
        };
    }

    private Consumer<T> rightClick() {
        return i -> {
            this.manager().delete(this.group, i.getCode(), i.getMarket());
            addedList.remove(i);
            toAddList.add(i);
            this.refreshLeftExcel();
            this.refreshRightExcel();
        };
    }

    private Consumer<T> toKlineView() {
        return d -> this.show(KlineViewPage.class, d);
    }

    private void refreshLeftExcel() {
        keyword = this.tText.getText().toString();
        toAddList.sort(Comparator.comparing(Item::getCode));
        this.toAdd.data(toAddList.stream().filter(this::leftFilter).collect(Collectors.toList()));
    }

    private void refreshRightExcel() {
        String rkw = this.aText.getText().toString();
        addedList.sort(Comparator.comparing(Item::getCode));
        if (StringUtils.isNotBlank(rkw)) {
            this.added.data(addedList.stream().filter(i -> i.getCode().contains(keyword) || i.getName().contains(keyword))
                    .collect(Collectors.toList()));
        } else {
            this.added.data(addedList);
        }
    }

    private String key(Item i) {
        return String.join(":", i.getCode(), i.getMarket());
    }

    protected abstract BaseItemGroupManager manager();

    protected abstract BaseItemManager<T> itemManager();

    protected abstract void extraViews(LinearLayout left);

    protected abstract void extraBehaviours();

    protected abstract void extraColumns(List<ExcelView.D<T>> rightDs);

    protected abstract boolean leftFilter(T i);

}
