package com.nature.stock.http;

import com.nature.common.enums.Market;

public class ShStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return "m:1+t:2,m:1+t:23";
    }

    @Override
    protected String market() {
        return Market.SH.getCode();
    }

}
