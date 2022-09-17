package com.nature.stock.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.util.HttpUtil;
import com.nature.stock.model.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IndustryStockHttp {

    private static final String URL_LIST = "http://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=100000&np=1&fid=f12" +
            "&fs=b:%s+f:!50&fields=f12,f13,f14";

    public List<Stock> list(String industry) {
        String response = HttpUtil.doGet(String.format(URL_LIST, industry),
                lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("no data from http");
        JSONArray ks = data.getJSONArray("diff");
        List<Stock> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            list.add(this.genItem(industry, ks.getJSONObject(i)));
        }
        return list;
    }

    private Stock genItem(String industry, JSONObject jo) {
        Stock i = new Stock();
        i.setCode(jo.getString("f12"));
        i.setMarket(jo.getString("f13"));
        i.setIndustry(industry);
        return i;
    }

}
