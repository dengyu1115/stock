package com.nature.stock.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.util.HttpUtil;
import com.nature.stock.model.Industry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IndustryHttp {

    private static final String URL = "https://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000&np=1&fid=f12" +
            "&fs=m:90+t:2+f:!50&fields=f12,f14";

    public List<Industry> list() {
        String response = HttpUtil.doGet(URL, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("no data from http");
        JSONArray ks = data.getJSONArray("diff");
        List<Industry> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            JSONObject o = ks.getJSONObject(i);
            Industry in = new Industry();
            in.setCode(o.getString("f12"));
            in.setName(o.getString("f14"));
            list.add(in);
        }
        return list;
    }
}
