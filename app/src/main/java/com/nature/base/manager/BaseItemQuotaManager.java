package com.nature.base.manager;

import com.nature.base.enums.RateDefType;
import com.nature.base.model.Item;
import com.nature.base.model.ItemGroup;
import com.nature.base.model.ItemLine;
import com.nature.base.model.RateDef;
import com.nature.common.calculator.QuotaCalculator;
import com.nature.common.model.Quota;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.stock.model.ItemQuota;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseItemQuotaManager<T extends Item, I extends ItemLine> {

    public List<ItemQuota> list(String rateType, String group, String date, String keyword) {
        if (StringUtils.isBlank(rateType)) {
            throw new RuntimeException("type is blank");
        }
        if (StringUtils.isBlank(group)) {
            throw new RuntimeException("group is blank");
        }
        List<RateDef> list = this.listRateDef(rateType, date);
        List<T> items = this.listItem(group, keyword);
        return RemoteExeUtil.exec(() -> items, i -> this.itemHandle(list, i));
    }

    private ItemQuota itemHandle(List<RateDef> list, Item i) {
        ItemQuota iq = new ItemQuota();
        iq.setCode(i.getCode());
        iq.setMarket(i.getMarket());
        iq.setName(i.getName());
        List<Quota> qs = new ArrayList<>();
        iq.setList(qs);
        for (RateDef j : list) {
            List<I> nets = this.itemLineManager().listAsc(i.getCode(), i.getMarket(), j.getDateStart(), j.getDateEnd());
            qs.add(QuotaCalculator.calculate(nets, I::getDate, this.latestFunc(), this.lowFunc(), this.highFunc()));
        }
        return iq;
    }

    private List<T> listItem(String group, String keyword) {
        List<T> list = this.itemManager().list();
        List<ItemGroup> itemGroups = this.itemGroupManager().listByGroup(group);
        Set<String> set = itemGroups.stream().map(this::key).collect(Collectors.toSet());
        List<T> items = list.stream().filter(i -> set.contains(this.key(i))
                && (i.getCode().contains(keyword) || i.getName().contains(keyword))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(items)) {
            throw new RuntimeException("stocks is empty");
        }
        return items;
    }

    private List<RateDef> listRateDef(String rateType, String date) {
        List<RateDef> list = this.rateDefManager().list(rateType);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("rate ref list is empty");
        }
        for (RateDef i : list) {
            if (RateDefType.PERIOD.getCode().equals(i.getType())) {
                i.setDateStart(CommonUtil.addDays(CommonUtil.formatDate(new Date()), -i.getDays()));
            } else if (StringUtils.isBlank(i.getDateStart())) {
                i.setDateStart(date);
            }
        }
        return list;
    }

    private String key(Item i) {
        return String.join(":", i.getCode(), i.getMarket());
    }

    protected abstract BaseRateDefManager rateDefManager();

    protected abstract BaseItemManager<T> itemManager();

    protected abstract BaseItemGroupManager itemGroupManager();

    protected abstract BaseItemLineManager<I> itemLineManager();

    protected abstract Function<I, Double> latestFunc();

    protected abstract Function<I, Double> highFunc();

    protected abstract Function<I, Double> lowFunc();

}
