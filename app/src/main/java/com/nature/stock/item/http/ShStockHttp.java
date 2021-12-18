package com.nature.stock.item.http;

import com.nature.stock.common.enums.ItemType;
import com.nature.stock.common.enums.Market;

public class ShStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return "m:1+t:2,m:1+t:23";
    }

    @Override
    protected String market() {
        return Market.SH.getCode();
    }

    @Override
    protected String type() {
        return ItemType.STOCK.getCode();
    }
}
