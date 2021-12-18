package com.nature.stock.func.manager;

import com.nature.stock.common.ioc.annotation.Injection;
import com.nature.stock.common.manager.WorkdayManager;
import com.nature.stock.func.model.Mark;
import com.nature.stock.func.model.Target;
import com.nature.stock.item.http.KlineHttp;
import com.nature.stock.item.manager.ItemManager;
import com.nature.stock.item.manager.KlineManager;
import com.nature.stock.item.model.Kline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 目标
 * @author nature
 * @version 1.0.0
 * @since 2020/11/7 9:29
 */
public class TargetManager {

    @Injection
    private MarkManager markManager;
    @Injection
    private ItemManager itemManager;
    @Injection
    private KlineManager klineManager;
    @Injection
    private WorkdayManager workdayManager;
    @Injection
    private KlineHttp klineHttp;

    public List<Target> list(String type, String date) {
        // 取出全部的买入交易数据
        List<Mark> list = markManager.list();
        if (list.isEmpty()) return new ArrayList<>();
        Map<String, Mark> markMap = list.stream()
                .filter(i -> i.getDate().compareTo(date) <= 0)
                .sorted(Comparator.comparing(Mark::getDate))
                .collect(Collectors.toMap(Mark::getCode, i -> i, (o, n) -> n));
        // 获取K线数据进行比对
        List<Kline> ks = this.listKline(date);
        if (ks.isEmpty()) return new ArrayList<>();
        List<Target> targets = new ArrayList<>();
        if ("0".equals(type)) {
            for (Kline k : ks) {
                Target target = this.markToTarget(markMap, k);
                if (target == null) continue;
                targets.add(target);
            }
        } else {
            boolean isBuy = this.getIsBuy(type);
            Function<Mark, Double> getRate = isBuy ? Mark::getRateBuy : Mark::getRateSell;
            BiFunction<Double, Double, Boolean> compare = isBuy ? (a, b) -> a < b : (a, b) -> a > b;
            for (Kline k : ks) {
                Target target = this.markToTarget(markMap, k, getRate, compare);
                if (target == null) continue;
                targets.add(target);
            }
        }
        return targets;
    }

    private boolean getIsBuy(String type) {
        if ("1".equals(type)) return true;
        if ("2".equals(type)) return false;
        throw new RuntimeException("类型不合法");
    }

    private List<Kline> listKline(String date) {
        return workdayManager.doInTradeTimeOrNot(() -> klineHttp.listLatest(), () -> klineManager.listByDate(date));
    }

    private Target markToTarget(Map<String, Mark> tradeMap, Kline k, Function<Mark, Double> getRate,
                                BiFunction<Double, Double, Boolean> compare) {
        Mark mark = tradeMap.get(k.getCode());
        if (mark == null) return null;
        Double p1 = k.getLatest();
        Double p2 = mark.getPrice();
        Double rate = getRate.apply(mark);
        double r = (p1 - p2) / p2;
        if (compare.apply(rate, r)) return null;
        Target target = new Target();
        target.setCode(mark.getCode());
        target.setMarket(mark.getMarket());
        target.setDate(k.getDate());
        target.setPrice(p1);
        target.setRate(r);
        target.setMark(mark);
        return target;
    }

    private Target markToTarget(Map<String, Mark> tradeMap, Kline k) {
        Mark mark = tradeMap.get(k.getCode());
        if (mark == null) return null;
        Double p1 = k.getLatest();
        Double p2 = mark.getPrice();
        double r = (p1 - p2) / p2;
        Target target = new Target();
        target.setCode(mark.getCode());
        target.setMarket(mark.getMarket());
        target.setDate(k.getDate());
        target.setPrice(p1);
        target.setRate(r);
        target.setMark(mark);
        return target;
    }

}
