package com.nature.stock.manager;

import com.nature.common.constant.Constant;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.func.manager.WorkdayManager;
import com.nature.stock.http.PriceKlineHttp;
import com.nature.stock.mapper.PriceMapper;
import com.nature.base.model.Item;
import com.nature.stock.model.Price;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;

@Component
public class PriceManager {

    @Injection
    private PriceKlineHttp priceKlineHttp;
    @Injection
    private PriceMapper priceMapper;
    @Injection
    private StockManager stockManager;
    @Injection
    private WorkdayManager workdayManager;

    public int reload() {
        priceMapper.delete();
        return RemoteExeUtil.exec(stockManager::list, this::reload).stream().mapToInt(i -> i).sum();
    }

    public int load() {
        return workdayManager.doInTradeTimeOrNot(date -> {
            throw new RuntimeException("交易时间不可同步数据");
        }, date -> RemoteExeUtil.exec(stockManager::list, this::load).stream().mapToInt(i -> i).sum());
    }

    public List<Price> list(String code, String market) {
        return priceMapper.list(code, market);
    }

    public List<Price> list(String code, String market, String start, String end) {
        return priceMapper.list(code, market, start, end);
    }

    public List<Price> listByDate(String date, String keyWord) {
        return priceMapper.listByDate(date, keyWord);
    }

    private int load(Item item) {
        String code = item.getCode();
        String market = item.getMarket();
        Price price = priceMapper.findLatest(code, market);
        String start = this.getLastDate(price), end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Price> list = priceKlineHttp.list(code, market, start, end);
        return priceMapper.batchMerge(list);
    }

    private int reload(Item item) {
        String start = "", end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Price> list = priceKlineHttp.list(item.getCode(), item.getMarket(), start, end);
        return priceMapper.batchMerge(list);
    }

    private String getLastDate(Price kline) {
        return kline == null ? "" : CommonUtil.addDays(kline.getDate(), 1).replace("-", "");
    }

}
