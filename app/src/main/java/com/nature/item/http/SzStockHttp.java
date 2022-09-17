package com.nature.item.http;

import com.nature.common.enums.ItemType;
import com.nature.stock.enums.Market;

public class SzStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return "m:0+t:6,m:0+t:80";
    }

    @Override
    protected String market() {
        return Market.SZ.getCode();
    }

    @Override
    protected String type() {
        return ItemType.STOCK.getCode();
    }
}
