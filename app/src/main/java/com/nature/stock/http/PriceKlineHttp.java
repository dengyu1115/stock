package com.nature.stock.http;

import com.nature.base.model.Price;
import com.nature.base.model.Val;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.util.TextUtil;

@Component
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
        Val val = new Val();
        i.setVal(val);
        val.setOpen(TextUtil.getDouble(s[1]));
        val.setLatest(TextUtil.getDouble(s[2]));
        val.setHigh(TextUtil.getDouble(s[3]));
        val.setLow(TextUtil.getDouble(s[4]));
        i.setShare(TextUtil.getDouble(s[5]));
        i.setAmount(TextUtil.getDouble(s[6]));
        return i;
    }

}
