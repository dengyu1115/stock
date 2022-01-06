package com.nature.item.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.util.HttpUtil;
import com.nature.common.util.TextUtil;
import com.nature.item.model.Scale;

import java.util.ArrayList;
import java.util.List;

/**
 * 净值数据 网络接口类
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 18:20
 */
public class ScaleHttp {
    /**
     * 链接地址：所有项目列表
     */
    private static final String URL = "https://fundmobapi.eastmoney.com/FundMApi/FundAssetsList.ashx?FCODE=%s" +
            "&pageIndex=1&pageSize=%s&version=1.0.0&deviceid=Wap&plat=Wap&product=EFund";

    /**
     * 查询基金估值数据
     * @param code 编号
     * @param size 数量
     * @return list
     */
    public List<Scale> list(String code, int size) {
        String uri = String.format(URL, code, size);
        String response = HttpUtil.doGet(uri, lines -> {
            StringBuilder builder = new StringBuilder();
            lines.forEach(i -> builder.append(i.trim()));
            return builder.toString();
        });
        JSONObject jo = JSON.parseObject(response);
        JSONArray ks = jo.getJSONArray("Datas");
        if (ks == null) throw new RuntimeException("数据缺失：" + code);
        List<Scale> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            Scale d = this.genScale(ks.getJSONObject(i), code);
            if (d != null) list.add(d);
        }
        return list;
    }

    private Scale genScale(JSONObject jo, String code) {
        String date = jo.getString("FSRQ");
        if (date == null) return null;
        Double amount = TextUtil.getDouble(jo.getString("NETNAV"));
        if (amount == null) return null;
        Double change = TextUtil.getDouble(jo.getString("CHANGE"));
        Scale scale = new Scale();
        scale.setCode(code);
        scale.setDate(date);
        scale.setAmount(amount);
        scale.setChange(change);
        return scale;
    }

}
