package com.nature.stock.page;

import com.nature.base.manager.BaseGroupManager;
import com.nature.base.manager.BaseItemQuotaManager;
import com.nature.base.manager.BaseRateDefManager;
import com.nature.base.manager.BaseRateTypeManager;
import com.nature.base.page.BaseItemQuotaPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.GroupManager;
import com.nature.stock.manager.ItemQuotaManager;
import com.nature.stock.manager.RateDefManager;
import com.nature.stock.manager.RateTypeManager;
import com.nature.stock.model.Net;
import com.nature.stock.model.Stock;

@PageView(name = "涨幅查看", group = "股票", col = 1, row = 6)
public class ItemQuotaPage extends BaseItemQuotaPage<Stock, Net> {

    @Injection
    private GroupManager groupManager;
    @Injection
    private RateTypeManager rateTypeManager;
    @Injection
    private RateDefManager rateDefManager;
    @Injection
    private ItemQuotaManager itemQuotaManager;


    @Override
    protected BaseItemQuotaManager<Stock, Net> manager() {
        return this.itemQuotaManager;
    }

    @Override
    protected BaseRateDefManager rateDefManager() {
        return this.rateDefManager;
    }

    @Override
    protected BaseRateTypeManager rateTypeManager() {
        return this.rateTypeManager;
    }

    @Override
    protected BaseGroupManager groupManager() {
        return this.groupManager;
    }
}
