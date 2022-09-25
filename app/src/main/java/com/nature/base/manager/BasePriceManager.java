package com.nature.base.manager;

import com.nature.base.mapper.BasePriceMapper;
import com.nature.base.model.Item;
import com.nature.base.model.Price;
import com.nature.common.constant.Constant;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.func.manager.WorkdayManager;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;

public abstract class BasePriceManager<T extends Item> {

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

    public List<Price> list(String code, String market) {
        return this.mapper().list(code, market);
    }

    public List<Price> list(String code, String market, String start, String end) {
        return this.mapper().list(code, market, start, end);
    }

    public List<Price> listByDate(String date, String keyWord) {
        return this.mapper().listByDate(date, keyWord);
    }

    private int load(Item item) {
        String code = item.getCode();
        String market = item.getMarket();
        Price price = this.mapper().findLatest(code, market);
        String start = this.getLastDate(price), end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Price> list = this.listFromHttp(code, market, start, end);
        return this.mapper().batchMerge(list);
    }

    private int reload(Item item) {
        String start = "", end = DateFormatUtils.format(new Date(), Constant.FORMAT_DAY);
        List<Price> list = this.listFromHttp(item.getCode(), item.getMarket(), start, end);
        return this.mapper().batchMerge(list);
    }

    private String getLastDate(Price kline) {
        return kline == null ? "" : CommonUtil.addDays(kline.getDate(), 1).replace("-", "");
    }

    protected abstract BasePriceMapper mapper();

    protected abstract BaseItemManager<T> itemManager();

    protected abstract List<Price> listFromHttp(String code, String market, String start, String end);
}
