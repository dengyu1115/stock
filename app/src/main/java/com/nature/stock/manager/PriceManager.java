package com.nature.stock.manager;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.manager.BasePriceManager;
import com.nature.base.mapper.BasePriceMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.http.PriceKlineHttp;
import com.nature.stock.mapper.PriceMapper;
import com.nature.stock.model.Stock;

import java.util.List;

@Component
public class PriceManager extends BasePriceManager<Stock> {

    @Injection
    private PriceKlineHttp priceKlineHttp;
    @Injection
    private PriceMapper priceMapper;
    @Injection
    private StockManager stockManager;

    @Override
    protected BasePriceMapper mapper() {
        return this.priceMapper;
    }

    @Override
    protected BaseItemManager<Stock> itemManager() {
        return this.stockManager;
    }

    @Override
    protected List<com.nature.base.model.Price> listFromHttp(String code, String market, String start, String end) {
        return priceKlineHttp.list(code, market, start, end);
    }

}
