package com.nature.stock.page;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.model.Quota;
import com.nature.common.page.Page;
import com.nature.common.util.PopUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.common.view.ViewTemplate;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.manager.GroupManager;
import com.nature.stock.manager.ItemQuotaManager;
import com.nature.stock.manager.RateDefManager;
import com.nature.stock.manager.RateTypeManager;
import com.nature.stock.model.Group;
import com.nature.stock.model.ItemQuota;
import com.nature.stock.model.RateDef;
import com.nature.stock.model.RateType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ItemQuotaPage extends Page {

    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int C = 0, S = 1, E = 2;
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private final RateTypeManager rateTypeManager = InstanceHolder.get(RateTypeManager.class);
    private final RateDefManager rateDefManager = InstanceHolder.get(RateDefManager.class);
    private final ItemQuotaManager itemQuotaManager = InstanceHolder.get(ItemQuotaManager.class);
    private Context context;
    private final List<ExcelView.D<ItemQuota>> ds = Arrays.asList(
            new ExcelView.D<>("??????", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(ItemQuota::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(ItemQuota::getCode))
    );
    private LinearLayout page;
    private LinearLayout body;
    private ExcelView<ItemQuota> excel;
    private Selector<RateType> ruleSel;
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

    private List<ExcelView.D<ItemQuota>> getDs(RateType def) {
        List<ExcelView.D<ItemQuota>> list = new ArrayList<>(ds);
        String code = def.getCode();
        if (StringUtils.isBlank(code)) {
            return list;
        }
        List<RateDef> rds = rateDefManager.list(code);
        int num = 0;
        for (RateDef rd : rds) {
            ExcelView.D<ItemQuota> d = new ExcelView.D<>(rd.getTitle(), C, Arrays.asList(
                    this.d("??????-??????", num, Quota::getDateStart),
                    this.d("??????-??????", num, Quota::getDateEnd),
                    this.d("??????", num, Quota::getOpen, TextUtil::price),
                    this.d("??????", num, Quota::getLatest, TextUtil::price),
                    this.d("??????", num, Quota::getHigh, TextUtil::price),
                    this.d("??????", num, Quota::getLow, TextUtil::price),
                    this.d("??????", num, Quota::getAvg, TextUtil::price),
                    this.d("??????", num, Quota::getRateLH, TextUtil::hundred),
                    this.d("??????", num, Quota::getRateHL, TextUtil::hundred),
                    this.d("???-??????", num, Quota::getRateOpen, TextUtil::hundred),
                    this.d("???-??????", num, Quota::getRateLow, TextUtil::hundred),
                    this.d("???-??????", num, Quota::getRateHigh, TextUtil::hundred),
                    this.d("???-??????", num, Quota::getRateAvg, TextUtil::hundred),
                    this.d("%-??????", num, Quota::getRatioAvg, TextUtil::hundred),
                    this.d("%-??????", num, Quota::getRatioLatest, TextUtil::hundred)
            ));
            list.add(d);
            num++;
        }
        return list;
    }

    private ExcelView.D<ItemQuota> d(String title, int i, Function<Quota, String> func) {
        return new ExcelView.D<>(title, d -> TextUtil.text(func.apply(d.getList().get(i))), C, E,
                Sorter.nullsLast(d -> func.apply(d.getList().get(i))));
    }

    private ExcelView.D<ItemQuota> d(String title, int i, Function<Quota, Double> func, Function<Double, String> text) {
        return new ExcelView.D<>(title, d -> text.apply(func.apply(d.getList().get(i))), C, E,
                Sorter.nullsLast(d -> func.apply(d.getList().get(i))));
    }

    private String getRateType() {
        RateType rateType = ruleSel.getValue();
        return rateType == null ? null : rateType.getCode();
    }

    private String getSelectorValue() {
        return selector.getValue();
    }

    private String getEditTextValue() {
        return editText.getText().toString();
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        density = metrics.density;
        page.setOrientation(LinearLayout.VERTICAL);
        this.header();
        this.body();
        this.footer();
    }

    private void initBehaviours() {
        selector.mapper(s -> s == null ? "--?????????--" : s).init().refreshData(this.initSelectorData());
        ruleSel.mapper(RateType::getTitle).onChangeRun(this::changeRule).init().refreshData(this.define());
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
        rb = template.button("??????", 50, 30);
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
        LinearLayout footer = new LinearLayout(context);
        page.addView(footer);
        footer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (density * 20)));
        footer.setGravity(Gravity.CENTER);
        total = new TextView(context);
        footer.addView(total);
        total.setGravity(Gravity.CENTER);
    }

    private void changeRule() {
        if (excel != null) {
            body.removeView(excel);
        }
        excel = new ExcelView<>(context, 10);
        excel.define(this.getDs(ruleSel.getValue()));
        body.addView(excel);
        this.refreshData();
    }

    private List<RateType> define() {
        List<RateType> list = rateTypeManager.list();
        RateType rateType = new RateType();
        rateType.setTitle("--?????????--");
        list.add(0, rateType);
        return list;
    }

    private List<Group> groups() {
        List<Group> list = groupManager.list();
        Group group = new Group();
        group.setName("--?????????--");
        list.add(0, group);
        return list;
    }

    private void refreshData() {
        new Thread(() -> {
            rb.setClickable(false);
            try {
                excel.data(this.listData());
                this.refreshTotal();
            } catch (Exception e) {
                Looper.prepare();
                PopUtil.alert(context, e.getMessage());
            } finally {
                rb.setClickable(true);
            }
        }).start();
    }

    private List<String> initSelectorData() {
        List<String> list = workDayManager.listWorkDays(workDayManager.getLatestWorkDay());
        list.add(0, null);
        return list;
    }

    protected List<ItemQuota> listData() {
        String rateType = this.getRateType();
        String group = this.getGroup();
        String date = this.getSelectorValue();
        String keyword = this.getEditTextValue();
        return itemQuotaManager.list(rateType, group, date, keyword);
    }

    private String getGroup() {
        Group group = groupSel.getValue();
        return group == null ? null : group.getCode();
    }

    private void refreshTotal() {
        handler.sendMessage(new Message());
    }

    @Override
    protected void makeStructure(LinearLayout page, Context context) {
        this.context = context;
        this.page = page;
        this.makeStructure();
    }

    @Override
    protected void onShow() {
        this.initBehaviours();
    }
}
