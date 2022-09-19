package com.nature.stock.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.util.HttpUtil;
import com.nature.base.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseKlineHttp<T extends Item> {

    private static final String URL = "http://push2his.eastmoney.com/api/qt/stock/kline/get?secid=%s.%s" +
            "&fields1=f1,f2,f3,f4,f5&fields2=f51,f52,f53,f54,f55,f56,f57&klt=101&fqt=%s&beg=%s&end=%s";

    public List<T> list(String code, String market, String start, String end) {
        String uri = String.format(URL, market, code, this.type(), start, end);
        String response = HttpUtil.doGet(uri, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) {
            throw new RuntimeException("data is null：" + code + market);
        }
        JSONArray ks = data.getJSONArray("klines");
        if (ks == null) {
            throw new RuntimeException("klines is null：" + code + market);
        }
        List<T> list = new ArrayList<>();
        for (Object datum : ks) list.add(this.genKline(code, market, (String) datum));
        return list;
    }

    protected abstract String type();

    protected abstract T genKline(String code, String market, String line);

}
