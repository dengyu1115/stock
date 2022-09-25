package com.nature.index.page;

import com.nature.base.manager.BaseGroupManager;
import com.nature.base.manager.BaseItemQuotaManager;
import com.nature.base.manager.BaseRateDefManager;
import com.nature.base.manager.BaseRateTypeManager;
import com.nature.base.model.Item;
import com.nature.base.model.Net;
import com.nature.base.page.BaseItemQuotaPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.GroupManager;
import com.nature.index.manager.ItemQuotaManager;
import com.nature.index.manager.RateDefManager;
import com.nature.index.manager.RateTypeManager;

@PageView(name = "涨幅查看", group = "指数", col = 1, row = 5)
public class ItemQuotaPage extends BaseItemQuotaPage<Item, Net> {

    @Injection
    private GroupManager groupManager;
    @Injection
    private RateTypeManager rateTypeManager;
    @Injection
    private RateDefManager rateDefManager;
    @Injection
    private ItemQuotaManager itemQuotaManager;


    @Override
    protected BaseItemQuotaManager<Item, Net> manager() {
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
