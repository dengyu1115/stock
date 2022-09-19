package com.nature.item.activity;

import android.widget.Button;
import android.widget.EditText;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.item.manager.ScaleManager;
import com.nature.item.model.Scale;

import java.util.Arrays;
import java.util.List;

/**
 * 规模
 * @author nature
 * @version 1.0.0
 * @since 2020/11/21 12:13
 */
public class ScaleListActivity extends BaseListActivity<Scale> {

    private final ScaleManager scaleManager = InstanceHolder.get(ScaleManager.class);

    private String name;
    private final List<ExcelView.D<Scale>> ds = Arrays.asList(
            new ExcelView.D<>("name", d -> TextUtil.text(name == null ? d.getName() : name), C, S),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(Scale::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, C, CommonUtil.nullsLast(Scale::getDate)),
            new ExcelView.D<>("金额", d -> TextUtil.amount(d.getAmount()), C, E, CommonUtil.nullsLast(Scale::getAmount)),
            new ExcelView.D<>("变动", d -> TextUtil.percent(d.getChange()), C, E, CommonUtil.nullsLast(Scale::getChange))
    );
    private EditText keyword;
    private Button reload;

    @Override
    protected List<ExcelView.D<Scale>> define() {
        return ds;
    }

    @Override
    protected List<Scale> listData() {
        String code = this.getIntent().getStringExtra("code");
        name = this.getIntent().getStringExtra("name");
        if (code == null) {
            return scaleManager.listLatest(keyword.getText().toString());
        }
        return scaleManager.listByCode(code);
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("重新加载", 80, 30));
        String code = this.getIntent().getStringExtra("code");
        if (code == null) {
            searchBar.addConditionView(keyword = template.editText(80, 30));
        }
    }

    @Override
    protected void initHeaderBehaviours() {
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> {
                            ClickUtil.asyncClick(v, () -> String.format("加载完成,共%s条", scaleManager.reloadAll()));
                            this.refreshData();
                        }
                )
        );
    }

}
