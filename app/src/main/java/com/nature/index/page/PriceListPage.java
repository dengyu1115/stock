package com.nature.index.page;

import com.nature.base.manager.BasePriceManager;
import com.nature.base.model.Item;
import com.nature.base.page.BasePriceDetailPage;
import com.nature.base.page.BasePriceListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.PriceManager;

@PageView(name = "K线不复权", group = "指数", col = 1, row = 2)
public class PriceListPage extends BasePriceListPage<Item> {

    @Injection
    private PriceManager priceManager;

    @Override
    protected BasePriceManager<Item> manager() {
        return this.priceManager;
    }

    @Override
    protected Class<? extends BasePriceDetailPage<Item>> jumpPage() {
        return PriceDetailPage.class;
    }

}
