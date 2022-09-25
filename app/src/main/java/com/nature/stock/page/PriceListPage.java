package com.nature.stock.page;

import com.nature.base.manager.BasePriceManager;
import com.nature.base.page.BasePriceDetailPage;
import com.nature.base.page.BasePriceListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.PriceManager;
import com.nature.stock.model.Stock;

@PageView(name = "K线不复权", group = "股票", col = 1, row = 3)
public class PriceListPage extends BasePriceListPage<Stock> {

    @Injection
    private PriceManager priceManager;

    @Override
    protected BasePriceManager<Stock> manager() {
        return this.priceManager;
    }

    @Override
    protected Class<? extends BasePriceDetailPage<Stock>> jumpPage() {
        return PriceDetailPage.class;
    }

}
