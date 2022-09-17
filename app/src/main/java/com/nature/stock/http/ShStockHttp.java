package com.nature.stock.http;

import com.nature.common.ioc.annotation.Component;
import com.nature.stock.enums.Market;

@Component
public class ShStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return Market.SH.getFs();
    }

    @Override
    protected String exchange() {
        return Market.SH.getCode();
    }

}
