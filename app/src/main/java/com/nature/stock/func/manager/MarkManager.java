package com.nature.stock.func.manager;

import com.nature.stock.common.ioc.annotation.Injection;
import com.nature.stock.common.manager.WorkdayManager;
import com.nature.stock.func.mapper.MarkMapper;
import com.nature.stock.func.model.Mark;
import com.nature.stock.common.util.CommonUtil;
import com.nature.stock.item.manager.KlineManager;
import com.nature.stock.item.model.Item;
import com.nature.stock.item.model.Kline;

import java.util.List;

/**
 * 交易
 * @author nature
 * @version 1.0.0
 * @since 2020/11/7 9:29
 */
public class MarkManager {

    @Injection
    private MarkMapper markMapper;
    @Injection
    private KlineManager klineManager;
    @Injection
    private WorkdayManager workdayManager;

    public int merge(Mark d) {
        return markMapper.merge(d);
    }

    public List<Mark> list() {
        List<Mark> list = markMapper.list();
        this.calTarget(list);
        return list;
    }

    private void calTarget(List<Mark> list) {
        for (Mark mark : list) {
            mark.setPriceBuy(mark.getPrice() * (1 + mark.getRateBuy()));
            mark.setPriceSell(mark.getPrice() * (1 + mark.getRateSell()));
        }
    }

    public List<Mark> list(String code, String market) {
        List<Mark> list = markMapper.list(code, market);
        this.calTarget(list);
        return list;
    }

    public Mark find(String code, String market, String date) {
        return markMapper.find(code, market, date);
    }

    public int delete(String code, String market, String date) {
        return markMapper.delete(code, market, date);
    }

    public Mark recommend(Item item) {
        String latestWorkDay = workdayManager.getLatestWorkDay();
        String aMonthAgo = CommonUtil.addMonths(latestWorkDay, -1);
        List<Kline> list = klineManager.list(item.getCode(), item.getMarket(), aMonthAgo, latestWorkDay);
        if (list.isEmpty()) return null;
        Mark mark = new Mark();
        Kline first = list.get(0);
        String date = first.getDate();
        Double max = first.getHigh(), min = first.getLow();
        for (Kline k : list) {
            Double high = k.getHigh();
            if (high > max) {
                max = high;
                date = k.getDate();
            }
            Double low = k.getLow();
            if (low < min) {
                min = low;
            }
        }
        mark.setCode(item.getCode());
        mark.setMarket(item.getMarket());
        mark.setDate(date);
        mark.setPrice(max);
        mark.setType(item.getType());
        mark.setRateBuy(-(max - min) / min * 0.618);
        mark.setRateSell(-(min - max) / max * 0.618);
        return mark;
    }
}
