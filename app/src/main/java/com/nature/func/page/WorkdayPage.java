package com.nature.func.page;

import android.annotation.SuppressLint;
import android.widget.Button;
import com.nature.common.constant.Constant;
import com.nature.common.enums.DateType;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.func.manager.WorkdayManager;
import com.nature.func.model.Month;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@PageView(name = "工作日", group = "基础", col = 1, row = 1)
@SuppressLint("DefaultLocale")
public class WorkdayPage extends ListPage<Month> {

    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private Button reload, loadLatest;
    private Selector<String> year;

    @Override
    protected List<ExcelView.D<Month>> define() {
        List<ExcelView.D<Month>> ds = new ArrayList<>();
        ds.add(new ExcelView.D<>("月份", i -> TextUtil.text(i.getMonth()), C, C, CommonUtil.nullsLast(Month::getMonth)));
        for (int i = 1; i < 32; i++) {
            String day = String.format("%02d", i);
            ds.add(new ExcelView.D<>(day, d -> TextUtil.text(this.getDateType(d, day)), C, C));
        }
        return ds;
    }

    private String getDateType(Month m, String day) {
        String date = String.format("%s-%s", m.getMonth(), day);
        String type = m.getDateType(date);
        return DateType.codeToName(type);
    }

    @Override
    protected List<Month> listData() {
        String date = this.year.getValue();
        if (StringUtils.isBlank(date)) return new ArrayList<>();
        return workDayManager.listYearMonths(date.substring(0, 4));
    }


    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("重新加载", 60, 30));
        searchBar.addConditionView(loadLatest = template.button("加载最新", 60, 30));
        searchBar.addConditionView(year = template.selector(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        year.mapper(i -> i).init().refreshData(this.initYears());
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> {
                            String year = this.year.getValue();
                            if (StringUtils.isBlank(year)) {
                                throw new RuntimeException("请选择年份");
                            }
                            ClickUtil.asyncClick(v, () -> String.format("加载完成,共%s条",
                                    workDayManager.reloadAll(year)));
                        }
                )
        );
        loadLatest.setOnClickListener(v ->
                ClickUtil.asyncClick(v, () -> {
                    String year = this.year.getValue();
                    if (StringUtils.isBlank(year)) {
                        throw new RuntimeException("请选择年份");
                    }
                    return String.format("加载完成,共%s条", workDayManager.loadLatest(year));
                }));
    }

    private List<String> initYears() {
        List<String> years = new ArrayList<>();
        Date now = new Date();
        for (int i = -10; i < 2; i++) {
            Date date = DateUtils.addYears(now, i);
            years.add(DateFormatUtils.format(date, Constant.FORMAT_YEAR));
        }
        return years;
    }

}
