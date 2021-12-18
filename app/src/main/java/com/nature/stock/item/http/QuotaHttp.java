package com.nature.stock.item.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nature.stock.common.util.HttpUtil;
import com.nature.stock.common.util.TextUtil;
import com.nature.stock.item.model.Quota;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 净值数据 网络接口类
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 18:20
 */
public class QuotaHttp {
    /**
     * 链接地址：所有项目列表
     */
    private static final String URL = "http://dcfm.eastmoney.com/em_mutisvcexpandinterface/api/js/get?type=GZFX_SCTJ" +
            "&filter=(MKTCODE=%27{code}%27)(TDATE%20%3E%20%27{date}%27)&st=TDATE&token=70f12f2f4f091e459a279469fe49eca5";

    /**
     * CHARSET
     */
    private static final String CHARSET = "utf8";

    /**
     * 日期
     */
    private String date;

    /**
     * 估值列表查询
     * @param code 编码
     * @param date 日期
     * @return list
     */
    public List<Quota> list(String code, String date) {
        String uri = URL.replace("{code}", code).replace("{date}", date);
        String response = HttpUtil.doGet(uri, CHARSET, lines -> lines.collect(Collectors.toList()).get(0));
        JSONArray ks = JSON.parseArray(response);
        if (ks == null) throw new RuntimeException(String.format("数据缺失code：%s date：%s", code, date));
        List<Quota> list = new ArrayList<>();
        for (int i = 0; i < ks.size(); i++) {
            Quota d = this.genQuota(ks.getJSONObject(i));
            if (d != null) list.add(d);
        }
        return list;
    }

    private Quota genQuota(JSONObject jo) {
        String code = jo.getString("MKTCODE");
        if (code == null) return null;
        String date = jo.getString("TDATE");
        if (date == null) return null;
        Double price = TextUtil.getDouble(jo.getString("NEW"));
        if (price == null) return null;   // 没有单位净值
        Double count = TextUtil.getDouble(jo.getString("SSGS_Count"));
        if (count == null) return null; // 没有估算值
        Double syl = TextUtil.getDouble(jo.getString("SYLAVG"));
        if (syl == null) return null; // 没有估算值
        Double gbZ = TextUtil.getDouble(jo.getString("ZGB"));
        if (gbZ == null) return null; // 没有估算值
        Double szZ = TextUtil.getDouble(jo.getString("ZSZ"));
        if (szZ == null) return null; // 没有估算值
        Double gbLt = TextUtil.getDouble(jo.getString("LTGB"));
        if (gbLt == null) return null; // 没有估算值
        Double szLt = TextUtil.getDouble(jo.getString("LTSZ"));
        if (szLt == null) return null; // 没有估算值
        Quota i = new Quota();
        i.setCode(code);    // 编号
        i.setDate(date.replace("T00:00:00", ""));
        i.setSyl(syl);
        i.setCount(count);
        i.setPrice(price);
        i.setSzZ(szZ);
        i.setSzLt(szLt);
        i.setGbZ(gbZ);
        i.setGbLt(gbLt);
        return i;
    }

}
