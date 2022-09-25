package com.nature.stock.http;

import com.nature.base.model.Net;
import com.nature.base.model.Val;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.util.TextUtil;

@Component
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
        Val val = new Val();
        i.setNet(val);
        val.setOpen(TextUtil.getDouble(s[1]));
        val.setLatest(TextUtil.getDouble(s[2]));
        val.setHigh(TextUtil.getDouble(s[3]));
        val.setLow(TextUtil.getDouble(s[4]));
        return i;
    }

}
