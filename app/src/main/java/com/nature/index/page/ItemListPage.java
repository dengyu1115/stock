package com.nature.index.page;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.model.Item;
import com.nature.base.page.BaseItemListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.index.manager.ItemManager;

import java.util.List;
import java.util.stream.Collectors;

@PageView(name = "指数", group = "指数", col = 1, row = 1)
public class ItemListPage extends BaseItemListPage<Item> {
    @Injection
    private ItemManager itemManager;

    protected List<Item> listData() {
        String keyword = this.keyword.getText().toString();
        return itemManager.list().stream().filter(i -> i.getCode().contains(keyword) || i.getName().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    protected BaseItemManager<Item> manager() {
        return this.itemManager;
    }

    @Override
    protected void extraColumns(List<ExcelView.D<Item>> list) {
    }

    @Override
    protected void extraViews(SearchBar searchBar) {
    }

    @Override
    protected void extraBehaviours() {
    }

}
