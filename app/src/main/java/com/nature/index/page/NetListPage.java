package com.nature.index.page;

import com.nature.base.manager.BaseNetManager;
import com.nature.base.model.Item;
import com.nature.base.page.BaseNetDetailPage;
import com.nature.base.page.BaseNetListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.NetManager;

@PageView(name = "K线-复权", group = "指数", col = 1, row = 3)
public class NetListPage extends BaseNetListPage<Item> {

    @Injection
    private NetManager netManager;

    @Override
    protected BaseNetManager<Item> manager() {
        return this.netManager;
    }

    @Override
    protected Class<? extends BaseNetDetailPage<Item>> jumpPage() {
        return NetDetailPage.class;
    }

}
