package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.http.PeHttp;
import com.nature.stock.mapper.PeMapper;
import com.nature.stock.model.Pe;
import com.nature.stock.model.Stock;

import java.util.Comparator;
import java.util.List;

@Component
public class PeManager {

    @Injection
    private PeHttp peHttp;
    @Injection
    private PeMapper peMapper;
    @Injection
    private StockManager stockManager;
    @Injection
    private WorkdayManager workdayManager;

    public int reload() {
        peMapper.delete();
        return RemoteExeUtil.exec(stockManager::list, this::reload).stream().mapToInt(i -> i).sum();
    }

    public int load() {
        return workdayManager.doInTradeTimeOrNot(date -> {
            throw new RuntimeException("交易时间不可同步数据");
        }, date -> RemoteExeUtil.exec(stockManager::list, this::load).stream().mapToInt(i -> i).sum());
    }

    public List<Pe> list(String code, String exchange) {
        return peMapper.list(code, exchange);
    }

    public List<Pe> listAsc(String code, String market, String start, String end) {
        return peMapper.list(code, market, start, end);
    }

    public List<Pe> list(String code, String exchange, String start, String end) {
        List<Pe> list = peMapper.list(code, exchange, start, end);
        list.sort(Comparator.comparing(Pe::getDate));
        return list;
    }

    public List<Pe> listByDate(String date, String keyWord) {
        return peMapper.listByDate(date, keyWord);
    }

    private int load(Stock item) {
        String code = item.getCode();
        String exchange = this.getExchange(item);
        if (exchange == null) {
            return 0;
        }
        Pe net = peMapper.findLatest(code, exchange);
        String month = this.getLastDate(net);
        List<Pe> list = peHttp.list(code, exchange, month);
        return peMapper.batchMerge(list);
    }

    private int reload(Stock item) {
        String exchange = this.getExchange(item);
        if (exchange == null) {
            return 0;
        }
        List<Pe> list = peHttp.list(item.getCode(), exchange);
        return peMapper.batchMerge(list);
    }

    private String getExchange(Stock item) {
        String exchange = item.getExchange();
        if ("sz".equals(exchange)) {
            return "sz";
        } else if ("sh".equals(exchange)) {
            return "sh";
        } else {
            return null;
        }
    }

    private String getLastDate(Pe i) {
        return i == null ? "all" : "" + (CommonUtil.monthBefore(i.getDate()) + 1);
    }

}
