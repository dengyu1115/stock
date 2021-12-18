package com.nature.stock.item.http;

import com.nature.stock.common.enums.ItemType;
import com.nature.stock.common.enums.Market;

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
