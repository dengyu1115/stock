package com.nature.stock.manager;

import com.nature.common.calculator.AvgCalculator;
import com.nature.common.constant.Constant;
import com.nature.common.db.DB;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.ExeUtil;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.http.NetKlineHttp;
import com.nature.stock.mapper.NetMapper;
import com.nature.stock.model.Item;
import com.nature.stock.model.Net;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NetManager {

    private static final int BATCH_SIZE = 70;

    @Injection
    private NetKlineHttp netKlineHttp;
    @Injection
    private NetMapper netMapper;
    @Injection
    private ItemManager itemManager;
    @Injection
    private WorkdayManager workdayManager;

    public int reload() {
        return ExeUtil.exec(netMapper::delete, itemManager::list, this::reload);
    }

    public int load() {
        return workdayManager.doInTradeTimeOrNot(date -> {
            throw new RuntimeException("交易时间不可同步数据");
        }, date -> ExeUtil.exec(itemManager::list, this::load));
    }

    public List<Net> list(String code, String market) {
        return netMapper.list(code, market);
    }

    public List<Net> list(String code, String market, String start, String end) {
        return netMapper.list(code, market, start, end);
    }

    public List<Net> listByDate(String date, String keyWord) {
        return netMapper.listByDate(date, keyWord);
    }

    private int load(Item item) {
        String code = item.getCode();
        String market = item.getMarket();
        Net net = netMapper.findLatest(code, market);
        String start = this.getLastDate(net), end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Net> list = netKlineHttp.list(code, market, start, end);
        if (!list.isEmpty()) {
            if (net == null) {
                List<Net> nets = netMapper.listBefore(code, market, start, 252);
                Collections.reverse(nets);
                nets.addAll(list);
                AvgCalculator.cal(nets);
            } else {
                AvgCalculator.cal(list);
            }
        }
        return this.batchMerge(list);
    }

    private int reload(Item item) {
        String start = "", end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Net> list = netKlineHttp.list(item.getCode(), item.getMarket(), start, end);
        AvgCalculator.cal(list);
        return this.batchMerge(list);
    }

    private String getLastDate(Net kline) {
        return kline == null ? "" : CommonUtil.addDays(kline.getDate(), 1).replace("-", "");
    }

    private int batchMerge(List<Net> list) {
        if (list == null || list.isEmpty()) return 0;
        return DB.create("nature/stock.db").batchExec(list, BATCH_SIZE, netMapper::batchMerge);
    }

}
