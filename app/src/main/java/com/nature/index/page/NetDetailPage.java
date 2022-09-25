package com.nature.index.page;

import com.nature.base.manager.BaseNetManager;
import com.nature.base.model.Item;
import com.nature.base.page.BaseNetDetailPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.NetManager;

@PageView(name = "K线-复权明细", group = "指数", col = 0, row = 0)
public class NetDetailPage extends BaseNetDetailPage<Item> {

    @Injection
    private NetManager netManager;

    @Override
    protected BaseNetManager<Item> manager() {
        return this.netManager;
    }

}
