package com.nature.stock.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.util.HttpUtil;
import com.nature.stock.model.Pe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PeHttp {

    private static final String URL = "https://eniu.com/chart/pea/%s%s/t/%s";

    public List<Pe> list(String code, String exchange) {
        return this.list(code, exchange, "all");
    }

    public List<Pe> list(String code, String exchange, String month) {
        String uri = String.format(URL, exchange, code, month);
        String response = HttpUtil.doGet(uri, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONArray dates = jo.getJSONArray("date");
        JSONArray pes = jo.getJSONArray("pe_ttm");
        if (dates == null || pes == null) {
            throw new RuntimeException("data is null：" + code + exchange);
        }
        List<Pe> list = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            String date = dates.getString(i);
            Double pe = pes.getDouble(i);
            list.add(this.getPe(code, exchange, date, pe));
        }
        return list;
    }

    private Pe getPe(String code, String exchange, String date, Double pe) {
        Pe i = new Pe();
        i.setCode(code);
        i.setMarket(exchange);
        i.setDate(date);
        i.setPe(pe);
        return i;
    }

}
