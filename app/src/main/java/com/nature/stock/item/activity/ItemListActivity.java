package com.nature.stock.item.activity;

import android.widget.Button;
import android.widget.EditText;
import com.nature.stock.common.activity.BaseListActivity;
import com.nature.stock.common.enums.ItemType;
import com.nature.stock.common.enums.Market;
import com.nature.stock.common.util.*;
import com.nature.stock.common.view.ExcelView;
import com.nature.stock.common.view.SearchBar;
import com.nature.stock.item.manager.ItemManager;
import com.nature.stock.item.model.Item;

import java.util.Arrays;
import java.util.List;

public class ItemListActivity extends BaseListActivity<Item> {

    private final List<ExcelView.D<Item>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(Item::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(Item::getCode)),
            new ExcelView.D<>("市场", d -> Market.codeToName(d.getMarket()), C, E, Sorter.nullsLast(Item::getMarket)),
            new ExcelView.D<>("类型", d -> ItemType.codeToName(d.getType()), C, E, Sorter.nullsLast(Item::getType))
    );
    private final ItemManager itemManager = InstanceHolder.get(ItemManager.class);
    private EditText keyword;
    private Button reload;

    protected List<Item> listData() {
        return itemManager.listByKeyWord(this.keyword.getText().toString());
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
                            String s = String.format("加载完成,共%s条", itemManager.reloadAll());
                            this.refreshData();
                            return s;
                        })
                )
        );
    }

    @Override
    protected List<ExcelView.D<Item>> define() {
        return ds;
    }

}
