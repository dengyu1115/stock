package com.nature.func.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;
import com.nature.common.constant.DefType;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.common.view.ViewTemplate;
import com.nature.func.manager.DefinitionManager;
import com.nature.func.manager.FundRateManager;
import com.nature.func.manager.WorkdayManager;
import com.nature.func.model.Definition;
import com.nature.func.model.FundRate;
import com.nature.func.model.RateDef;
import com.nature.item.activity.ScaleListActivity;
import com.nature.item.manager.GroupManager;
import com.nature.item.model.Group;
import com.nature.item.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class FundRateActivity extends AppCompatActivity {

    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int C = 0, S = 1, E = 2;
    private final DefinitionManager definitionManager = InstanceHolder.get(DefinitionManager.class);
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final FundRateManager fundRateManager = InstanceHolder.get(FundRateManager.class);
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private Context context;
    private final List<ExcelView.D<FundRate>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(FundRate::getName), kline()),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(FundRate::getCode), priceNet()),
            new ExcelView.D<>("标记", d -> TextUtil.text("+"), C, C, toMark()),
            new ExcelView.D<>("规模", d -> TextUtil.amount(d.getScale()), C, E, Sorter.nullsLast(FundRate::getScale), scale()),
            new ExcelView.D<>("净值", d -> TextUtil.net(d.getNet()), C, E, Sorter.nullsLast(FundRate::getNet), lineView()),
            new ExcelView.D<>("增长率", d -> TextUtil.hundred(d.getRate()), C, E, Sorter.nullsLast(FundRate::getRate)),
            new ExcelView.D<>("总净值", d -> TextUtil.net(d.getNetTotal()), C, E, Sorter.nullsLast(FundRate::getNetTotal)),
            new ExcelView.D<>("总增长", d -> TextUtil.hundred(d.getRateTotal()), C, E, Sorter.nullsLast(FundRate::getRateTotal))
    );
    private LinearLayout page;
    private LinearLayout body;
    private ExcelView<FundRate> excel;
    private Selector<Definition> ruleSel;
    private Selector<Group> groupSel;
    private Selector<String> selector;
    private EditText editText;
    private TextView total;
    private final Handler handler = new Handler(msg -> {
        this.total.setText(String.valueOf(this.excel.getListSize()));
        return false;
    });
    private Button rb;
    private ViewTemplate template;
    private int height;
    private float density;

    private List<ExcelView.D<FundRate>> getDs(Definition def) {
        List<ExcelView.D<FundRate>> list = new ArrayList<>(ds);
        String json = def.getJson();
        if (StringUtils.isBlank(json)) return list;
        for (RateDef i : JSON.parseArray(json, RateDef.class)) {
            list.add(new ExcelView.D<>(i.getTitle(), d -> {
                Double rate = d.getRate(i.getCode());
                return TextUtil.hundred(rate);
            }, C, E, Sorter.nullsLast(d -> d.getRate(i.getCode()))));
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.initActivity(this);
        this.context = FundRateActivity.this;
        this.makeStructure();
        this.initBehaviours();
        this.setContentView(page);
        this.refreshData();
    }

    private Definition getDefinition() {
        return ruleSel.getValue();
    }

    private String getSelectorValue() {
        return selector.getValue();
    }

    private String getEditTextValue() {
        return editText.getText().toString();
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        page = template.linearPage();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        density = metrics.density;
        this.header();
        this.body();
        this.footer();
    }

    private void initBehaviours() {
        selector.mapper(s -> s).init().refreshData(this.initSelectorData());
        ruleSel.mapper(Definition::getTitle).onChangeRun(this::changeRule).init().refreshData(this.define());
        groupSel.mapper(Group::getName).init().refreshData(this.groups());
        rb.setOnClickListener(v -> this.refreshData());
    }

    private void header() {
        LinearLayout header;
        page.addView(header = new LinearLayout(context));
        header.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (density * 40)));
        SearchBar searchBar = new SearchBar(context);
        header.addView(searchBar);
        selector = template.selector(80, 30);
        ruleSel = template.selector(80, 30);
        groupSel = template.selector(80, 30);
        editText = template.editText(80, 30);
        rb = template.button("查询", 50, 30);
        searchBar.addConditionView(ruleSel);
        searchBar.addConditionView(groupSel);
        searchBar.addConditionView(selector);
        searchBar.addConditionView(editText);
        searchBar.addHandleView(rb);
    }

    private void body() {
        page.addView(body = new LinearLayout(context));
        body.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height - (int) (density * 60)));
    }

    private void footer() {
        LinearLayout footer;
        page.addView(footer = new LinearLayout(context));
        footer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (density * 20)));
        footer.setGravity(Gravity.CENTER);
        total = new TextView(context);
        footer.addView(total);
        total.setGravity(Gravity.CENTER);
    }

    private void changeRule() {
        if (excel != null) body.removeView(excel);
        excel = new ExcelView<>(context, 9);
        excel.define(this.getDs(ruleSel.getValue()));
        body.addView(excel);
        this.refreshData();
    }

    private List<Definition> define() {
        List<Definition> list = definitionManager.list(DefType.FUND_LIST);
        Definition definition = new Definition();
        definition.setTitle("--请选择--");
        list.add(0, definition);
        return list;
    }

    private List<Group> groups() {
        List<Group> list = groupManager.list(ItemType.FUND.getCode());
        Group group = new Group();
        group.setName("--请选择--");
        list.add(0, group);
        return list;
    }

    private void refreshData() {
        new Thread(() -> {
            excel.data(this.listData());
            this.refreshTotal();
        }).start();
    }

    private List<String> initSelectorData() {
        return workDayManager.listWorkDays(workDayManager.getLatestWorkDay());
    }

    protected List<FundRate> listData() {
        Definition definition = this.getDefinition();
        Group group = this.getGroup();
        String date = this.getSelectorValue();
        String keyword = this.getEditTextValue();
        return fundRateManager.list(definition, group, date, keyword);
    }

    private Group getGroup() {
        return groupSel.getValue();
    }

    private Consumer<FundRate> priceNet() {
        return d -> {
            Intent intent = new Intent(context, PriceNetActivity.class);
            intent.putExtra("code", d.getCode());
            this.startActivity(intent);
        };
    }

    private Consumer<FundRate> kline() {
        return d -> {
            Intent intent = new Intent(context, FundLineActivity.class);
            Item item = new Item();
            item.setName(d.getName());
            item.setCode(d.getCode());
            intent.putExtra("fund", JSON.toJSONString(item));
            this.startActivity(intent);
        };
    }

    private Consumer<FundRate> lineView() {
        return d -> {
            Intent intent = new Intent(context, FundLineActivity.class);
            intent.putExtra("fund", JSON.toJSONString(d));
            this.startActivity(intent);
        };
    }

    private Consumer<FundRate> scale() {
        return d -> {
            Intent intent = new Intent(context, ScaleListActivity.class);
            intent.putExtra("code", d.getCode());
            intent.putExtra("name", d.getName());
            this.startActivity(intent);
        };
    }

    private Consumer<FundRate> toMark() {
        return d -> {
            Intent intent = new Intent(context, MarkListActivity.class);
            intent.putExtra("type", ItemType.FUND.getCode());
            intent.putExtra("item", JSON.toJSONString(d));
            this.startActivity(intent);
        };
    }

    private void refreshTotal() {
        handler.sendMessage(new Message());
    }

}
