package com.nature.stock.http;

import com.nature.common.ioc.annotation.Component;
import com.nature.stock.enums.Market;

@Component
public class SzStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return Market.SZ.getFs();
    }

    @Override
    protected String exchange() {
        return Market.SZ.getCode();
    }

}
