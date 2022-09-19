package com.nature.item.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.constant.Constant;
import com.nature.common.util.HttpUtil;
import com.nature.common.util.TextUtil;
import com.nature.func.model.PriceNet;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 价格 网络接口
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 18:20
 */
public class PriceHttp {
    /**
     * 链接地址：所有项目列表
     */
    private static final String URL_LATEST = "https://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000&np=1&fltt=2" +
            "&fs=b:MK0010,b:MK0021,b:MK0022,b:MK0023,b:MK0024,b:MK0404,b:MK0405,b:MK0406,b:MK0407" +
            "&fid=f12&fields=f12,f14,f2,f15,f16,f18,f6,f21,f124";

    /**
     * CHARSET
     */
    private static final String CHARSET = "utf8";

    /**
     * 日期
     */
    private String date;

    /**
     * 按日期查询列表
     * @param date 日期
     * @return list
     */
    public List<PriceNet> list(String date) {
        String response = HttpUtil.doGet(URL_LATEST, CHARSET, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("网路数据缺失");
        JSONArray ks = data.getJSONArray("diff");
        this.date = date;
        List<PriceNet> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            PriceNet kline = this.genKline(ks.getJSONObject(i));
            if (kline != null) list.add(kline); // 过滤不合格数据
        }
        return list;
    }

    private PriceNet genKline(JSONObject jo) {
        Double time = TextUtil.getDouble(jo.getString("f124"));
        if (time == null) return null;
        String date = DateFormatUtils.format(new Date((long) (time * 1000L)), Constant.FORMAT_DATE);
        if (!Objects.equals(date, this.date)) return null; // 不是当天的数据不要
        String code = jo.getString("f12");
        if (code == null) return null;  // 没有编号
        String name = jo.getString("f14");
        if (name == null) return null;  // 没有名称
        Double priceHigh = TextUtil.getDouble(jo.getString("f15"));
        Double priceLow = TextUtil.getDouble(jo.getString("f16"));
        Double priceLast = TextUtil.getDouble(jo.getString("f18"));
        if (priceLast == null) return null; // 没有上次收盘报价
        Double priceLatest = TextUtil.getDouble(jo.getString("f2"));
        if (priceLatest == null || priceLatest.equals(0.0d)) return null;   // 没有最新报价
        Double amount = TextUtil.getDouble(jo.getString("f6"));
        Double scale = TextUtil.getDouble(jo.getString("f21"));
        PriceNet i = new PriceNet();
        i.setCode(code);
        i.setDate(date);
        i.setName(name);
        i.setPriceHigh(priceHigh);
        i.setPriceLow(priceLow);
        i.setPriceLast(priceLast);
        i.setPriceLatest(priceLatest);
        i.setAmount(amount);
        i.setScale(scale);
        return i;
    }

}
