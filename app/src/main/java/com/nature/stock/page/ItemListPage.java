package com.nature.stock.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.common.enums.Market;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.stock.manager.ItemManager;
import com.nature.stock.model.Item;

import java.util.Arrays;
import java.util.List;

public class ItemListPage extends ListPage<Item> {

    private final ItemManager itemManager = InstanceHolder.get(ItemManager.class);
    private final List<ExcelView.D<Item>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(Item::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(Item::getCode)),
            new ExcelView.D<>("市场", d -> Market.codeToName(d.getMarket()), C, E, Sorter.nullsLast(Item::getMarket))
    );
    private EditText keyword;
    private Button reload;

    protected List<Item> listData() {
        return itemManager.list(this.keyword.getText().toString());
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("加载最新", 80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", itemManager.reload());
                            this.refreshData();
                            return s;
                        })));
    }

    @Override
    protected List<ExcelView.D<Item>> define() {
        return ds;
    }

}
