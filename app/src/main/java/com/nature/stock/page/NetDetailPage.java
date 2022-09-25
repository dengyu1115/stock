package com.nature.stock.page;

import com.nature.base.manager.BaseNetManager;
import com.nature.base.page.BaseNetDetailPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.NetManager;
import com.nature.stock.model.Stock;

@PageView(name = "K线-复权明细", group = "股票", col = 0, row = 0)
public class NetDetailPage extends BaseNetDetailPage<Stock> {

    @Injection
    private NetManager netManager;

    @Override
    protected BaseNetManager<Stock> manager() {
        return this.netManager;
    }

}
