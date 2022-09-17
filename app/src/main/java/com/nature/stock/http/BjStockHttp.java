package com.nature.stock.http;

import com.nature.common.ioc.annotation.Component;
import com.nature.stock.enums.Market;

@Component
public class BjStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return Market.BJ.getFs();
    }

    @Override
    protected String exchange() {
        return Market.BJ.getCode();
    }

}
