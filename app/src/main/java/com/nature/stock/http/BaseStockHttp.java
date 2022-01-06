package com.nature.stock.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.util.HttpUtil;
import com.nature.stock.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseStockHttp {

    private static final String URL_LIST = "https://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000&np=1&fltt=2" +
            "&fs=%s&fid=f12&fields=f12,f13,f14,f266,f17,f2,f15,f16,f5,f6,f21,f124";

    public List<Item> list() {
        String response = HttpUtil.doGet(String.format(URL_LIST, this.fs()),
                lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("no data from http");
        JSONArray ks = data.getJSONArray("diff");
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) list.add(this.genItem(ks.getJSONObject(i)));
        return list;
    }

    private Item genItem(JSONObject jo) {
        Item item = new Item();
        item.setCode(jo.getString("f12"));
        item.setName(jo.getString("f14").replace(" ", ""));
        item.setMarket(this.market());
        return item;
    }

    protected abstract String fs();

    protected abstract String market();

}
