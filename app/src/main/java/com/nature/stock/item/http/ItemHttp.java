package com.nature.stock.item.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.stock.common.util.HttpUtil;
import com.nature.stock.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * etf day detail net
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 18:20
 */
public class ItemHttp {

    /**
     * 链接地址：所有项目列表
     */
    private static final String URL_LIST = "https://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000&np=1&fltt=2" +
            "&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048&fid=f12" +
            "&fields=f12,f13,f14,f266,f17,f2,f15,f16,f5,f6,f21,f124";
    /**
     * CHARSET
     */
    private static final String CHARSET = "utf8";

    public List<Item> list() {
        String response = HttpUtil.doGet(URL_LIST, CHARSET, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("网路数据缺失");
        JSONArray ks = data.getJSONArray("diff");
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) list.add(this.genItem(ks.getJSONObject(i)));
        return list;
    }

    private Item genItem(JSONObject jo) {
        Item item = new Item();
        item.setCode(jo.getString("f12"));
        item.setMarket(jo.getString("f13"));
        item.setType(jo.getString("f266"));
        item.setName(jo.getString("f14"));
        return item;
    }

}
