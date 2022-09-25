package com.nature.base.manager;

import com.nature.base.mapper.BaseNetMapper;
import com.nature.base.model.Item;
import com.nature.base.model.Net;
import com.nature.common.calculator.AvgCalculator;
import com.nature.common.constant.Constant;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.func.manager.WorkdayManager;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class BaseNetManager<T extends Item> implements BaseItemLineManager<Net> {

    @Injection
    private WorkdayManager workdayManager;

    public int reload() {
        this.mapper().delete();
        return RemoteExeUtil.exec(this.itemManager()::list, this::reload).stream().mapToInt(i -> i).sum();
    }

    public int load() {
        return workdayManager.doInTradeTimeOrNot(date -> {
            throw new RuntimeException("交易时间不可同步数据");
        }, date -> RemoteExeUtil.exec(this.itemManager()::list, this::load).stream().mapToInt(i -> i).sum());
    }

    public List<Net> list(String code, String market) {
        return this.mapper().list(code, market);
    }

    public List<Net> listAsc(String code, String market, String start, String end) {
        return this.mapper().list(code, market, start, end);
    }

    public List<Net> list(String code, String market, String start, String end) {
        List<Net> list = this.mapper().list(code, market, start, end);
        list.sort(Comparator.comparing(Net::getDate));
        return list;
    }

    public List<Net> listByDate(String date, String keyWord) {
        return this.mapper().listByDate(date, keyWord);
    }

    private int load(Item item) {
        String code = item.getCode();
        String market = item.getMarket();
        Net net = this.mapper().findLatest(code, market);
        String start = this.getLastDate(net), end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Net> list = this.listFromHttp(code, market, start, end);
        if (!list.isEmpty()) {
            if (net == null) {
                AvgCalculator.cal(list);
            } else {
                List<Net> nets = this.mapper().listBefore(code, market, start, 252);
                Collections.reverse(nets);
                nets.addAll(list);
                AvgCalculator.cal(nets);
            }
        }
        return this.mapper().batchMerge(list);
    }

    private int reload(Item item) {
        String start = "", end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Net> list = this.listFromHttp(item.getCode(), item.getMarket(), start, end);
        AvgCalculator.cal(list);
        return this.mapper().batchMerge(list);
    }

    private String getLastDate(Net kline) {
        return kline == null ? "" : CommonUtil.addDays(kline.getDate(), 1).replace("-", "");
    }

    protected abstract BaseNetMapper mapper();

    protected abstract BaseItemManager<T> itemManager();

    protected abstract List<Net> listFromHttp(String code, String market, String start, String end);

}
