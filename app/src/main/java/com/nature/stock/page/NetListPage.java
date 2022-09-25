package com.nature.stock.page;

import com.nature.base.manager.BaseNetManager;
import com.nature.base.page.BaseNetDetailPage;
import com.nature.base.page.BaseNetListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.NetManager;
import com.nature.stock.model.Stock;

@PageView(name = "K线-复权", group = "股票", col = 1, row = 4)
public class NetListPage extends BaseNetListPage<Stock> {

    @Injection
    private NetManager netManager;

    @Override
    protected BaseNetManager<Stock> manager() {
        return this.netManager;
    }

    @Override
    protected Class<? extends BaseNetDetailPage<Stock>> jumpPage() {
        return NetDetailPage.class;
    }

}
