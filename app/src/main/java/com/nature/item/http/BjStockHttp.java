package com.nature.item.http;

import com.nature.common.enums.ItemType;
import com.nature.common.enums.Market;

public class BjStockHttp extends BaseStockHttp {

    @Override
    protected String fs() {
        return "m:0+t:81+s:2048";
    }

    @Override
    protected String market() {
        return Market.BJ.getCode();
    }

    @Override
    protected String type() {
        return ItemType.STOCK.getCode();
    }
}
