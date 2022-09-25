package com.nature.index.page;

import android.widget.LinearLayout;
import com.nature.base.manager.BaseItemGroupManager;
import com.nature.base.manager.BaseItemManager;
import com.nature.base.model.Item;
import com.nature.base.page.BaseItemGroupPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.view.ExcelView;
import com.nature.index.manager.ItemGroupManager;
import com.nature.index.manager.ItemManager;

import java.util.List;

@PageView(name = "项目分组", group = "指数", col = 0, row = 0)
public class ItemGroupPage extends BaseItemGroupPage<Item> {

    @Injection
    private ItemManager itemManager;
    @Injection
    private ItemGroupManager itemGroupManager;

    @Override
    protected BaseItemGroupManager manager() {
        return this.itemGroupManager;
    }

    @Override
    protected BaseItemManager<Item> itemManager() {
        return this.itemManager;
    }

    @Override
    protected void extraViews(LinearLayout left) {
    }

    @Override
    protected void extraBehaviours() {
    }

    @Override
    protected void extraColumns(List<ExcelView.D<Item>> list) {
    }

    @Override
    protected boolean leftFilter(Item i) {
        return (i.getCode().contains(keyword) || i.getName().contains(keyword));
    }

}
