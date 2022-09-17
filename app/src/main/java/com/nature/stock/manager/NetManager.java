package com.nature.stock.manager;

import com.nature.common.calculator.AvgCalculator;
import com.nature.common.constant.Constant;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.http.NetKlineHttp;
import com.nature.stock.mapper.NetMapper;
import com.nature.stock.model.Item;
import com.nature.stock.model.Net;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
public class NetManager {

    @Injection
    private NetKlineHttp netKlineHttp;
    @Injection
    private NetMapper netMapper;
    @Injection
    private StockManager stockManager;
    @Injection
    private WorkdayManager workdayManager;

    public int reload() {
        netMapper.delete();
        return RemoteExeUtil.exec(stockManager::list, this::reload).stream().mapToInt(i -> i).sum();
    }

    public int load() {
        return workdayManager.doInTradeTimeOrNot(date -> {
            throw new RuntimeException("交易时间不可同步数据");
        }, date -> RemoteExeUtil.exec(stockManager::list, this::load).stream().mapToInt(i -> i).sum());
    }

    public List<Net> list(String code, String market) {
        return netMapper.list(code, market);
    }

    public List<Net> listAsc(String code, String market, String start, String end) {
        return netMapper.list(code, market, start, end);
    }

    public List<Net> list(String code, String market, String start, String end) {
        List<Net> list = netMapper.list(code, market, start, end);
        list.sort(Comparator.comparing(Net::getDate));
        return list;
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
                AvgCalculator.cal(list);
            } else {
                List<Net> nets = netMapper.listBefore(code, market, start, 252);
                Collections.reverse(nets);
                nets.addAll(list);
                AvgCalculator.cal(nets);
            }
        }
        return netMapper.batchMerge(list);
    }

    private int reload(Item item) {
        String start = "", end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Net> list = netKlineHttp.list(item.getCode(), item.getMarket(), start, end);
        AvgCalculator.cal(list);
        return netMapper.batchMerge(list);
    }

    private String getLastDate(Net kline) {
        return kline == null ? "" : CommonUtil.addDays(kline.getDate(), 1).replace("-", "");
    }

}
