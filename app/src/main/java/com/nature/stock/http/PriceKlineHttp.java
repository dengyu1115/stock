package com.nature.stock.http;

import com.nature.common.util.TextUtil;
import com.nature.stock.model.Price;

public class PriceKlineHttp extends BaseKlineHttp<Price> {

    @Override
    protected String type() {
        return "0";
    }

    @Override
    protected Price genKline(String code, String market, String line) {
        Price i = new Price();
        i.setCode(code);
        i.setMarket(market);
        String[] s = line.split(",");
        i.setDate(s[0]);
        i.setOpen(TextUtil.getDouble(s[1]));
        i.setLatest(TextUtil.getDouble(s[2]));
        i.setHigh(TextUtil.getDouble(s[3]));
        i.setLow(TextUtil.getDouble(s[4]));
        i.setShare(TextUtil.getDouble(s[5]));
        i.setAmount(TextUtil.getDouble(s[6]));
        return i;
    }

}
