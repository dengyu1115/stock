package com.nature.stock.item.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.stock.common.constant.Constant;
import com.nature.stock.common.util.HttpUtil;
import com.nature.stock.common.util.TextUtil;
import com.nature.stock.item.model.Kline;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * etf day detail net
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 18:20
 */
public class KlineHttp {

    /**
     * 链接地址：所有项目列表
     */
    private static final String URL_LIST = "https://push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000&np=1&fltt=2" +
            "&fs=b:MK0010,b:MK0021,b:MK0022,b:MK0023,b:MK0024,b:MK0404,b:MK0405,b:MK0406,b:MK0407" +
            "&fid=f12&fields=f12,f13,f14,f266,f17,f2,f15,f16,f5,f6,f21,f124";
    /**
     * 链接地址：K线列表
     */
    private static final String URL_KLINE = "http://push2his.eastmoney.com/api/qt/stock/kline/get?secid=%s.%s" +
            "&fields1=f1,f2,f3,f4,f5&fields2=f51,f52,f53,f54,f55,f56,f57&klt=101&fqt=2&beg=%s&end=%s";

    /**
     * 获取k线数据
     * @param code   code
     * @param market market
     * @param start  start
     * @param end    end
     * @return list
     */
    public List<Kline> list(String code, String market, String start, String end) {
        String uri = String.format(URL_KLINE, market, code, start, end);
        String response = HttpUtil.doGet(uri, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("历史K线数据缺失：" + code + market);
        JSONArray ks = data.getJSONArray("klines");
        if (ks == null) throw new RuntimeException("历史K线数据缺失：" + code + market);
        List<Kline> list = new ArrayList<>();
        for (Object datum : ks) list.add(this.genKline(code, market, (String) datum));
        return list;
    }

    public List<Kline> listLatest() {
        String response = HttpUtil.doGet(URL_LIST, lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject jo = JSON.parseObject(response);
        JSONObject data = jo.getJSONObject("data");
        if (data == null) throw new RuntimeException("网路数据缺失");
        JSONArray ks = data.getJSONArray("diff");
        List<Kline> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            Kline kline = this.genKline(ks.getJSONObject(i));
            if (kline == null) continue;
            list.add(kline);
        }
        return list;
    }

    private Kline genKline(JSONObject jo) {
        Kline kline = new Kline();
        Double time = TextUtil.getDouble(jo.getString("f124"));
        if (time == null) return null;
        String date = DateFormatUtils.format(new Date((long) (time * 1000L)), Constant.FORMAT_DATE);
        kline.setDate(date);
        kline.setCode(jo.getString("f12"));
        kline.setMarket(jo.getString("f13"));
        kline.setType(jo.getString("f266"));
        kline.setName(jo.getString("f14"));
        kline.setOpen(TextUtil.getDouble(jo.getString("f17")));
        kline.setLatest(TextUtil.getDouble(jo.getString("f2")));
        kline.setHigh(TextUtil.getDouble(jo.getString("f15")));
        kline.setLow(TextUtil.getDouble(jo.getString("f16")));
        kline.setShare(TextUtil.getDouble(jo.getString("f5")));
        kline.setAmount(TextUtil.getDouble(jo.getString("f6")));
        return kline;
    }

    private Kline genKline(String code, String market, String line) {
        String[] s = line.split(",");
        Kline kline = new Kline();
        kline.setCode(code);
        kline.setMarket(market);
        kline.setDate(s[0]);
        kline.setOpen(TextUtil.getDouble(s[1]));
        kline.setLatest(TextUtil.getDouble(s[2]));
        kline.setHigh(TextUtil.getDouble(s[3]));
        kline.setLow(TextUtil.getDouble(s[4]));
        kline.setShare(TextUtil.getDouble(s[5]));
        kline.setAmount(TextUtil.getDouble(s[6]));
        return kline;
    }

}
