package com.nature.index.manager;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.manager.BasePriceManager;
import com.nature.base.mapper.BasePriceMapper;
import com.nature.base.model.Item;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.index.http.PriceKlineHttp;
import com.nature.index.mapper.PriceMapper;

import java.util.List;

@Component
public class PriceManager extends BasePriceManager<Item> {

    @Injection
    private PriceKlineHttp priceKlineHttp;
    @Injection
    private PriceMapper priceMapper;
    @Injection
    private ItemManager itemManager;

    @Override
    protected BasePriceMapper mapper() {
        return this.priceMapper;
    }

    @Override
    protected BaseItemManager<Item> itemManager() {
        return this.itemManager;
    }

    @Override
    protected List<com.nature.base.model.Price> listFromHttp(String code, String market, String start, String end) {
        return priceKlineHttp.list(code, market, start, end);
    }

}
