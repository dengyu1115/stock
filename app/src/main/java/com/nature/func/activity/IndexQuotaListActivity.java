package com.nature.func.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.enums.DefaultQuota;
import com.nature.common.enums.QuotaField;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.model.ItemQuota;
import com.nature.item.activity.QuotaActivity;
import com.nature.item.manager.QuotaManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * 大盘指标
 * @author nature
 * @version 1.0.0
 * @since 2020/11/24 19:09
 */
public class IndexQuotaListActivity extends BaseListActivity<ItemQuota> {

    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private final List<ExcelView.D<ItemQuota>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(ItemQuota::getName), this.lineView()),
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
    private final QuotaManager quotaManager = InstanceHolder.get(QuotaManager.class);
    private Button dateStart, dateEnd;
    private Selector<String> codeSel, typeSel;

    @SuppressLint("ResourceType")
    private void onDateChooseClick(View v) {
        Button button = (Button) v;
        template.datePiker(button);
    }

    protected List<ItemQuota> listData() {
        String code = this.codeSel.getValue();
        String type = this.typeSel.getValue();
        String dateStart = this.dateStart.getText().toString();
        String dateEnd = this.dateEnd.getText().toString();
        return quotaManager.listToItems(code, type, dateStart, dateEnd);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        String end = CommonUtil.formatDate(new Date());
        String start = CommonUtil.addMonths(end, -1);
        searchBar.addConditionView(codeSel = template.selector(80, 30));
        searchBar.addConditionView(typeSel = template.selector(80, 30));
        searchBar.addConditionView(dateStart = template.button(start, 80, 30));
        searchBar.addConditionView(dateEnd = template.button(end, 80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        codeSel.mapper(this::getCodeName).init().refreshData(this.getCodes());
        typeSel.mapper(this::getTypeName).init().refreshData(this.getTypes());
        Button.OnClickListener listener = this::onDateChooseClick;
        dateStart.setOnClickListener(listener);
        dateEnd.setOnClickListener(listener);
    }

    private List<String> getCodes() {
        List<String> codes = DefaultQuota.codes();
        codes.add(0, null);
        return codes;
    }

    private List<String> getTypes() {
        List<String> codes = QuotaField.codes();
        codes.add(0, null);
        return codes;
    }

    private String getCodeName(String s) {
        String name = DefaultQuota.codeToName(s);
        if (StringUtils.isBlank(name)) {
            return "-请选择-";
        }
        return name;
    }

    private String getTypeName(String s) {
        String name = QuotaField.codeToName(s);
        if (StringUtils.isBlank(name)) {
            return "-请选择-";
        }
        return name;
    }

    @Override
    protected List<ExcelView.D<ItemQuota>> define() {
        return ds;
    }

    private Consumer<ItemQuota> lineView() {
        return d -> {
            Intent intent = new Intent(context, QuotaActivity.class);
            intent.putExtra("code", d.getCode());
            this.startActivity(intent);
        };
    }

}
