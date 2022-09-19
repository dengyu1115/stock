package com.nature.stock.manager;

import com.nature.base.manager.*;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.model.Net;
import com.nature.stock.model.Stock;

import java.util.function.Function;

@Component
public class ItemQuotaManager extends BaseItemQuotaManager<Stock, Net> {

    @Injection
    private RateDefManager rateDefManager;
    @Injection
    private StockManager stockManager;
    @Injection
    private NetManager netManager;
    @Injection
    private ItemGroupManager itemGroupManager;

    @Override
    protected BaseRateDefManager rateDefManager() {
        return this.rateDefManager;
    }

    @Override
    protected BaseItemManager<Stock> itemManager() {
        return this.stockManager;
    }

    @Override
    protected BaseItemGroupManager itemGroupManager() {
        return this.itemGroupManager;
    }

    @Override
    protected BaseItemLineManager<Net> itemLineManager() {
        return this.netManager;
    }

    @Override
    protected Function<Net, Double> highFunc() {
        return Net::getHigh;
    }

    @Override
    protected Function<Net, Double> lowFunc() {
        return Net::getLow;
    }

}
