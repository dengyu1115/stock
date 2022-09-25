package com.nature.stock.page;

import com.nature.base.manager.BasePriceManager;
import com.nature.base.page.BasePriceDetailPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.PriceManager;
import com.nature.stock.model.Stock;

@PageView(name = "K线-不复权明细", group = "股票", col = 0, row = 0)
public class PriceDetailPage extends BasePriceDetailPage<Stock> {

    @Injection
    private PriceManager priceManager;

    @Override
    protected BasePriceManager<Stock> manager() {
        return this.priceManager;
    }
}
