package com.nature.common.factory;

import android.annotation.SuppressLint;
import com.nature.common.function.Purchasable;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.CommonUtil;
import com.nature.func.manager.WorkdayManager;
import com.nature.item.manager.KlineManager;
import com.nature.item.model.Item;
import com.nature.item.model.Kline;

import java.util.List;

/**
 * 可购买功能工厂
 * @author nature
 * @version 1.0.0
 * @since 2020/12/20 22:09
 */
@SuppressLint("DefaultLocale")
public class PurchasableFactory {

    private static PurchasableFactory instance;
    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final KlineManager klineManager = InstanceHolder.get(KlineManager.class);

    private PurchasableFactory() {
    }

    public static PurchasableFactory getInstance() {
        if (instance == null) {
            synchronized (PurchasableFactory.class) {
                if (instance == null) {
                    instance = new PurchasableFactory();
                }
            }
        }
        return instance;
    }

    public Purchasable atOneMonthLow() {
        return new Purchasable() {
            private static final double RATE = 0.8;
            private static final String FORMAT = "近一个月，最高价：%.4f，最低价：%.4f，当前价：%.4f，大于：%.4f。";
            private final ThreadLocal<String> descriptions = new ThreadLocal<>();

            @Override
            public String getCode() {
                return "001";
            }

            @Override
            public String getName() {
                return descriptions.get();
            }

            @Override
            public boolean pass(Item item, double price) {
                return atMonthsLow(item, price, RATE, 1, FORMAT, descriptions);
            }
        };
    }

    public Purchasable atThreeMonthLow() {
        return new Purchasable() {
            private static final double RATE = 0.7;
            private static final String FORMAT = "近三个月，最高价：%.4f，最低价：%.4f，当前价：%.4f，大于：%.4f。";
            private final ThreadLocal<String> descriptions = new ThreadLocal<>();

            @Override
            public String getCode() {
                return "002";
            }

            @Override
            public String getName() {
                return descriptions.get();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public boolean pass(Item item, double price) {
                return atMonthsLow(item, price, RATE, 3, FORMAT, descriptions);
            }
        };
    }

    private boolean atMonthsLow(Item item, double price, double rate, int months, String format,
                                ThreadLocal<String> descriptions) {
        String timeEnd = workDayManager.getLatestWorkDay();
        String timeStart = CommonUtil.addMonths(timeEnd, -months);
        List<Kline> list = klineManager.list(item.getCode(), item.getMarket(), timeStart, timeEnd);
        if (list.isEmpty()) return false;
        Kline first = list.get(0);
        Double max = first.getHigh(), min = first.getLow();
        for (Kline k : list) {
            Double high = k.getHigh();
            if (high > max) {
                max = high;
            }
            Double low = k.getLow();
            if (low < min) {
                min = low;
            }
        }
        descriptions.set(String.format(format, max, min, price, rate));
        return (max - price) / (max - min) > rate;
    }

}
