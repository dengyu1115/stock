package com.nature.stock.page;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.page.Page;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.Selector;
import com.nature.common.view.ViewTemplate;
import com.nature.stock.enums.Market;
import com.nature.stock.manager.IndustryManager;
import com.nature.stock.manager.ItemGroupManager;
import com.nature.stock.manager.StockManager;
import com.nature.stock.model.Industry;
import com.nature.stock.model.Item;
import com.nature.stock.model.ItemGroup;
import com.nature.stock.model.Stock;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@PageView(name = "项目分组", group = "股票", col = 0, row = 0)
public class ItemGroupPage extends Page {

    private static final int CENTER = 0, START = 1;
    @Injection
    private IndustryManager industryManager;
    @Injection
    private StockManager stockManager;
    @Injection
    private ItemGroupManager itemGroupManager;
    private Context context;
    private LinearLayout page;
    private float uw, uh;
    private EditText tText, aText;
    private Button tQuery, aQuery;
    private ExcelView<Stock> toAdd, added;
    private Selector<String> exchange, industry;
    private ViewTemplate template;
    private String lkw;
    private String group;
    private Map<String, String> map;
    private List<Stock> toAddList, addedList;
    private final List<ExcelView.D<Stock>> lds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), CENTER, START, CommonUtil.nullsLast(Stock::getName), toKlineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), CENTER, START, CommonUtil.nullsLast(Stock::getCode), toKlineView()),
            new ExcelView.D<>("交易所", d -> TextUtil.text(Market.codeToName(d.getExchange())), C, E, Sorter.nullsLast(Stock::getExchange)),
            new ExcelView.D<>("行业", d -> TextUtil.text(this.map.get(d.getIndustry())), C, E, Sorter.nullsLast(Stock::getIndustry)),
            new ExcelView.D<>("操作", d -> "+", CENTER, CENTER, this.leftClick())
    );

    private final List<ExcelView.D<Stock>> rds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), CENTER, START, CommonUtil.nullsLast(Stock::getName), toKlineView()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), CENTER, START, CommonUtil.nullsLast(Stock::getCode)),
            new ExcelView.D<>("交易所", d -> TextUtil.text(Market.codeToName(d.getExchange())), C, E, Sorter.nullsLast(Stock::getExchange)),
            new ExcelView.D<>("行业", d -> TextUtil.text(this.map.get(d.getIndustry())), C, E, Sorter.nullsLast(Stock::getIndustry)),
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
        this.uw = metrics.widthPixels / 100f;
        this.uh = metrics.heightPixels / 100f;
        this.header();
        this.body();
    }

    private void initBehaviours() {
        this.tText.setText("");
        this.aText.setText("");
        List<String> exchanges = Arrays.stream(Market.values()).map(Market::getCode).collect(Collectors.toList());
        exchanges.add(0, null);
        exchange.mapper(this::getExchangeName).init().refreshData(exchanges);
        List<Industry> list = industryManager.list();
        this.map = list.stream().collect(Collectors.toMap(Industry::getCode, Industry::getName, (o, n) -> n));
        List<String> industries = list.stream().map(Industry::getCode).collect(Collectors.toList());
        industries.add(0, null);
        industry.mapper(this::getIndustryName).init().refreshData(industries);
        this.toAddList = stockManager.list();
        if (StringUtils.isBlank(this.group = this.getParam())) {
            this.addedList = new ArrayList<>();
        } else {
            this.addedList = stockManager.list(this.group, null);
        }
        if (!this.addedList.isEmpty()) {
            this.toAddList.removeAll(new HashSet<>(this.addedList));
        }
        this.tQuery.setOnClickListener(v -> this.refreshLeftExcel());
        this.aQuery.setOnClickListener(v -> this.refreshRightExcel());
        this.added.define(rds);
        this.toAdd.define(lds);
        this.toAdd.data(this.toAddList);
        this.added.data(this.addedList);
    }

    private void header() {
        LinearLayout layout = this.block(100, 10);
        page.addView(layout);
        LinearLayout left = this.block(50, 10);
        left.addView(tText = template.editText(100, 30));
        left.addView(exchange = template.selector(100, 30));
        left.addView(industry = template.selector(100, 30));
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

    private Consumer<Stock> leftClick() {
        return i -> {
            ItemGroup ig = new ItemGroup();
            ig.setGroup(this.group);
            ig.setCode(i.getCode());
            ig.setMarket(i.getMarket());
            itemGroupManager.merge(ig);
            addedList.add(i);
            toAddList.remove(i);
            this.refreshLeftExcel();
            this.refreshRightExcel();
        };
    }

    private Consumer<Stock> rightClick() {
        return i -> {
            itemGroupManager.delete(this.group, i.getCode(), i.getMarket());
            addedList.remove(i);
            toAddList.add(i);
            this.refreshLeftExcel();
            this.refreshRightExcel();
        };
    }

    private Consumer<Stock> toKlineView() {
        return d -> this.show(KlineViewPage.class, d);
    }

    private void refreshLeftExcel() {
        lkw = this.tText.getText().toString();
        toAddList.sort(Comparator.comparing(Item::getCode));
        this.toAdd.data(toAddList.stream().filter(this::letFilter).collect(Collectors.toList()));
    }

    private boolean letFilter(Stock i) {
        String exchange = this.exchange.getValue();
        String industry = this.industry.getValue();
        boolean match = true;
        if (StringUtils.isNotBlank(exchange)) {
            match = exchange.equals(i.getExchange());
        }
        if (StringUtils.isNotBlank(industry)) {
            match = match && industry.equals(i.getIndustry());
        }
        return match && (i.getCode().contains(lkw) || i.getName().contains(lkw));
    }

    private void refreshRightExcel() {
        String rkw = this.aText.getText().toString();
        addedList.sort(Comparator.comparing(Item::getCode));
        if (StringUtils.isNotBlank(rkw)) {
            this.added.data(addedList.stream().filter(i -> i.getCode().contains(lkw) || i.getName().contains(lkw))
                    .collect(Collectors.toList()));
        } else {
            this.added.data(addedList);
        }
    }

    private String getExchangeName(String code) {
        if (code == null) {
            return "--请选择--";
        }
        return Market.codeToName(code);
    }

    private String getIndustryName(String code) {
        if (code == null) {
            return "--请选择--";
        }
        return this.map.get(code);
    }

}
