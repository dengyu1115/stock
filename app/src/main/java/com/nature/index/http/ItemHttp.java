package com.nature.index.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.base.model.Item;
import com.nature.common.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemHttp {

    private static final String URL = "https://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000&np=1&fltt=2" +
            "&fs=m:1+s:2,m:0+t:5&fid=f12&fields=f12,f13,f14,f266,f17,f2,f15,f16,f5,f6,f21,f124";

    public List<Item> list() {
        String response = HttpUtil.doGet(URL, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) {
            throw new RuntimeException("no data from http");
        }
        JSONArray ks = data.getJSONArray("diff");
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            list.add(this.genItem(ks.getJSONObject(i)));
        }
        return list;
    }

    private Item genItem(JSONObject jo) {
        Item i = new Item();
        i.setCode(jo.getString("f12"));
        i.setName(jo.getString("f14").replace(" ", ""));
        i.setMarket(jo.getString("f13"));
        return i;
    }

}
