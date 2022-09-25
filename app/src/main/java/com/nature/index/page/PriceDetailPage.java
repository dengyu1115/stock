package com.nature.index.page;

import com.nature.base.manager.BasePriceManager;
import com.nature.base.model.Item;
import com.nature.base.page.BasePriceDetailPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.PriceManager;

@PageView(name = "K线-不复权明细", group = "指数", col = 0, row = 0)
public class PriceDetailPage extends BasePriceDetailPage<Item> {

    @Injection
    private PriceManager priceManager;

    @Override
    protected BasePriceManager<Item> manager() {
        return this.priceManager;
    }
}
