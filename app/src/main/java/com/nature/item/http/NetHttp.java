package com.nature.item.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.func.model.PriceNet;
import com.nature.common.util.HttpUtil;
import com.nature.common.util.TextUtil;
import com.nature.item.model.Net;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 净值数据 网络接口类
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 18:20
 */
public class NetHttp {
    /**
     * 链接地址：所有项目列表估值列表
     */
    private static final String URL_GZ = "https://api.fund.eastmoney.com/FundGuZhi/GetFundGZList?" +
            "type=%s&sort=1&orderType=asc&canbuy=0&pageIndex=1&pageSize=20000";

    /**
     * 链接地址：净值历史
     */
    private static final String URL_HISTORY = "http://api.fund.eastmoney.com/f10/lsjz?fundCode=%s" +
            "&pageIndex=1&pageSize=100000&startDate=%s&endDate=";

    /**
     * CHARSET
     */
    private static final String CHARSET = "utf8";
    private static final Map<String, String> HEADER = new HashMap<>();

    static {
        HEADER.put("Referer", "https://fund.eastmoney.com/fundguzhi.html");
    }

    /**
     * 日期
     */
    private String date;

    /**
     * 估值列表查询
     * @param type 基金类型标号
     * @param date 日期
     * @return list
     */
    public List<PriceNet> list(String type, String date) {
        String uri = String.format(URL_GZ, type);
        String response = HttpUtil.doGet(uri, CHARSET, HEADER, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("Data");
        if (data == null) throw new RuntimeException("数据缺失：" + type);
        this.date = date;
        JSONArray ks = data.getJSONArray("list");
        if (ks == null) throw new RuntimeException("数据缺失：" + type);
        List<PriceNet> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            PriceNet net = this.genPriceNet(ks.getJSONObject(i));
            if (net != null) list.add(net);
        }
        return list;
    }

    /**
     * 净值列表查询
     * @param type 基金类型标号
     * @return list
     */
    public List<Net> listByType(String type) {
        String uri = String.format(URL_GZ, type);
        String response = HttpUtil.doGet(uri, CHARSET, HEADER, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("Data");
        if (data == null) throw new RuntimeException("数据缺失：" + type);
        JSONArray ks = data.getJSONArray("list");
        if (ks == null) throw new RuntimeException("数据缺失：" + type);
        List<Net> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            Net net = this.genNet(ks.getJSONObject(i));
            if (net != null) list.add(net);
        }
        return list;
    }


    public List<Net> listByCode(String code) {
        return this.listByCode(code, "");
    }

    public List<Net> listByCode(String code, String date) {
        String uri = String.format(URL_HISTORY, code, date);
        String response = HttpUtil.doGet(uri, CHARSET, HEADER, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("Data");
        if (data == null) throw new RuntimeException("数据缺失：" + code);
        JSONArray ks = data.getJSONArray("LSJZList");
        if (ks == null) throw new RuntimeException("数据缺失：" + code);
        List<Net> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            Net net = this.genNet(code, ks.getJSONObject(i));
            if (net != null) list.add(net);
        }
        return list;
    }

    private PriceNet genPriceNet(JSONObject jo) {
        String date = jo.getString("gxrq");
        if (!Objects.equals(date, this.date)) return null;
        Double net = TextUtil.getDouble(jo.getString("dwjz"));
        if (net == null) return null;   // 没有单位净值
        Double netGz = TextUtil.getDouble(jo.getString("gsz"));
        if (netGz == null) return null; // 没有估算值
        PriceNet i = new PriceNet();
        i.setCode(jo.getString("bzdm"));    // 编号
        i.setDate(date);
        i.setNetLast(net);
        i.setNetLatest(netGz);
        return i;
    }

    private Net genNet(JSONObject jo) {
        String date = jo.getString("gxrq");
        if (date == null) return null;
        Double net = TextUtil.getDouble(jo.getString("gbdwjz"));
        if (net == null) return null;   // 没有单位净值
        Double rate = TextUtil.getDouble(jo.getString("jzzzl"));
        if (rate == null) return null;   // 没有净值增长率
        Net i = new Net();
        i.setCode(jo.getString("bzdm"));    // 编号
        i.setDate(date);
        i.setNet(net);
        i.setRate(rate / 100d);
        return i;
    }

    private Net genNet(String code, JSONObject jo) {
        String date = jo.getString("FSRQ");
        Double net = TextUtil.getDouble(jo.getString("DWJZ"));
        if (net == null) return null;   // 没有单位净值
        Double rate = TextUtil.getDouble(jo.getString("JZZZL"));
        if (rate == null) return null;   // 没有单位净值
        Net i = new Net();
        i.setCode(code);    // 编号
        i.setDate(date);
        i.setNet(net);
        i.setRate(rate / 100d);
        return i;
    }

}
