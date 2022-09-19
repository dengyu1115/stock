package com.nature.func.manager;

import com.nature.common.calculator.RateCalculator;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.model.RateResult;
import com.nature.func.model.ItemQuota;
import com.nature.item.manager.ItemGroupManager;
import com.nature.item.manager.KlineManager;
import com.nature.item.manager.NetManager;
import com.nature.item.model.Item;
import com.nature.item.model.Kline;
import com.nature.item.model.Net;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 项目指标
 * @author nature
 * @version 1.0.0
 * @since 2020/11/28 14:14
 */
public class ItemQuotaManager {

    @Injection
    private KlineManager klineManager;
    @Injection
    private ItemGroupManager itemGroupManager;
    @Injection
    private NetManager netManager;

    public List<ItemQuota> list(String group, String keyword, String dateStart, String dateEnd) {
        List<Item> list = itemGroupManager.listItem(group).stream()
                .filter(i -> i.getName().contains(keyword) || i.getCode().contains(keyword))
                .collect(Collectors.toList());
        if (list.isEmpty()) return new ArrayList<>();
        if (ItemType.FUND.getCode().equals(group)) {
            return list.parallelStream().map(i -> this.calculate(
                    i,
                    dateStart,
                    Net::getDate,
                    () -> netManager.list(i.getCode(), dateStart, dateEnd),
                    () -> netManager.findLast(i.getCode(), dateStart),
                    Net::getNetTotal,
                    Net::getNetTotal,
                    Net::getNetTotal)).filter(Objects::nonNull).sorted(this.comparator()).collect(Collectors.toList());
        } else {
            return list.parallelStream().map(i -> this.calculate(
                    i,
                    dateStart,
                    Kline::getDate,
                    () -> klineManager.list(i.getCode(), i.getMarket(), dateStart, dateEnd),
                    () -> klineManager.findLast(i.getCode(), i.getMarket(), dateStart),
                    Kline::getLatest,
                    Kline::getLow,
                    Kline::getHigh)).filter(Objects::nonNull).sorted(this.comparator()).collect(Collectors.toList());
        }
    }

    private Comparator<? super ItemQuota> comparator() {
        return (o1, o2) -> {
            double hl1 = o1.getRateHL();
            double hl2 = o2.getRateHL();
            double lh1 = o1.getRateLH();
            double lh2 = o2.getRateLH();
            if (hl2 == 0) {
                return 1;
            }
            if (hl1 == 0) {
                return -1;
            }
            return Double.compare(lh1 / hl1, lh2 / hl2);
        };
    }

    public <T> ItemQuota calculate(Item item, String dateStart, Function<T, String> getDate,
                                   Supplier<List<T>> getList, Supplier<T> getFirst,
                                   Function<T, Double> getPrice,
                                   Function<T, Double> getLow, Function<T, Double> getHigh) {
        List<T> ks = getList.get();
        if (ks.isEmpty()) return null;
        T first = getFirst.get();
        if (first == null && !dateStart.equals(getDate.apply(ks.get(0)))) return null;
        if (first == null) first = ks.get(0);
        ItemQuota quota = this.itemToQuota(item);
        double open = getPrice.apply(first), low = open, high = open, total = 0d, latest = getPrice.apply(ks.get(ks.size() - 1));
        for (T k : ks) {
            if (getLow.apply(k) < low) low = getLow.apply(k);
            if (getHigh.apply(k) > high) high = getHigh.apply(k);
            total += getPrice.apply(k);
        }
        double avg = total / (double) ks.size();
        double rateOpen = (latest - open) / open;
        double rateHigh = (latest - high) / high;
        double rateLow = (latest - low) / low;
        double rateAvg = (latest - avg) / avg;
        RateResult rateResult = RateCalculator.cal(ks, getPrice);
        double rateLH = rateResult.getMax();
        double rateHL = rateResult.getMin();
        double ratioLow = (high - low) == 0 ? 1d : (latest - low) / (high - low);
        double ratioAvg = (high - low) == 0 ? 1d : (avg - low) / (high - low);
        quota.setDateStart(getDate.apply(ks.get(0)));
        quota.setDateEnd(getDate.apply(ks.get(ks.size() - 1)));
        quota.setOpen(open);
        quota.setHigh(high);
        quota.setLow(low);
        quota.setLatest(latest);
        quota.setAvg(avg);
        quota.setRateOpen(rateOpen);
        quota.setRateHigh(rateHigh);
        quota.setRateLow(rateLow);
        quota.setRateAvg(rateAvg);
        quota.setRateLH(rateLH);
        quota.setRateHL(rateHL);
        quota.setRatioLatest(ratioLow);
        quota.setRatioAvg(ratioAvg);
        return quota;
    }

    private ItemQuota itemToQuota(Item item) {
        ItemQuota quota = new ItemQuota();
        quota.setCode(item.getCode());
        quota.setName(item.getName());
        quota.setMarket(item.getMarket());
        quota.setType(item.getType());
        return quota;
    }
}
