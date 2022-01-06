package com.nature.stock.http;

import com.nature.common.util.TextUtil;
import com.nature.stock.model.Net;

public class NetKlineHttp extends BaseKlineHttp<Net> {

    @Override
    protected String type() {
        return "2";
    }

    @Override
    protected Net genKline(String code, String market, String line) {
        Net i = new Net();
        i.setCode(code);
        i.setMarket(market);
        String[] s = line.split(",");
        i.setDate(s[0]);
        i.setOpen(TextUtil.getDouble(s[1]));
        i.setLatest(TextUtil.getDouble(s[2]));
        i.setHigh(TextUtil.getDouble(s[3]));
        i.setLow(TextUtil.getDouble(s[4]));
        return i;
    }

}
