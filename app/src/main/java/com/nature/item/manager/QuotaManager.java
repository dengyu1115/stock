package com.nature.item.manager;

import com.nature.common.db.BaseDB;
import com.nature.common.enums.DefaultQuota;
import com.nature.common.enums.QuotaField;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.TaskMethod;
import com.nature.func.manager.WorkdayManager;
import com.nature.common.util.LocalExeUtil;
import com.nature.func.manager.ItemQuotaManager;
import com.nature.func.model.ItemQuota;
import com.nature.item.http.QuotaHttp;
import com.nature.item.mapper.QuotaMapper;
import com.nature.item.model.Item;
import com.nature.item.model.Quota;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 净值
 * @author nature
 * @version 1.0.0
 * @since 2020/8/19 19:18
 */
public class QuotaManager {

    @Injection
    private QuotaMapper quotaMapper;

    @Injection
    private QuotaHttp quotaHttp;

    @Injection
    private WorkdayManager workDayManager;

    @Injection
    private ItemQuotaManager itemQuotaManager;

    public int reloadAll() {
        return LocalExeUtil.exec(quotaMapper::delete, DefaultQuota::codes, this::load);
    }

    @TaskMethod(value = "005", name = "加载最新指标")
    public int loadLatest() {
        return LocalExeUtil.exec(DefaultQuota::codes, this::load);
    }

    private int load(String code) {
        // 查询已有的第一条数据
        Quota first = quotaMapper.findFirstByCode(code), last = quotaMapper.findLastByCode(code);
        String dateStart = last == null ? "1999-12-31" : last.getDate();
        List<Quota> list = quotaHttp.list(code, dateStart);
        if (list.isEmpty()) return 0;
        this.calculateRate(list, first == null ? list.get(0) : first);
        return this.batchMerge(list);
    }

    public int batchMerge(List<Quota> list) {
        if (list == null || list.isEmpty()) return 0;
        return BaseDB.create().batchExec(list, 50, l -> quotaMapper.batchMerge(l));
    }

    private void calculateRate(List<Quota> list, Quota quota) {
        for (Quota q : list) {
            q.setSylRate(this.rate(q::getSyl, quota::getSyl));
            q.setPriceRate(this.rate(q::getPrice, quota::getPrice));
            q.setCountRate(this.rate(q::getCount, quota::getCount));
            q.setSzZRate(this.rate(q::getSzZ, quota::getSzZ));
            q.setGbZRate(this.rate(q::getGbZ, quota::getGbZ));
            q.setSzLtRate(this.rate(q::getSzLt, quota::getSzLt));
            q.setGbLtRate(this.rate(q::getGbLt, quota::getGbLt));
        }
    }

    private double rate(Supplier<Double> a, Supplier<Double> b) {
        Double ad = a.get(), bd = b.get();
        return (ad - bd) / bd;
    }

    public List<Quota> listByCode(String code) {
        return quotaMapper.listByCode(code);
    }

    public List<ItemQuota> listToItems(String code, String type, String dateStart, String dateEnd) {
        List<Item> items = this.items();
        if (StringUtils.isNotBlank(code)) {
            items = items.stream().filter(i -> i.getCode().equals(code)).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(type)) {
            items = items.stream().filter(i -> i.getType().equals(type)).collect(Collectors.toList());
        }
        Map<String, Function<Quota, Double>> funcMap = this.funcMap();
        Map<String, List<Quota>> listMap = items.stream().map(Item::getCode).distinct()
                .collect(Collectors.toMap(i -> i, i -> quotaMapper.list(i, dateStart, dateEnd)));
        Map<String, Quota> quotaMap = items.stream().map(Item::getCode).distinct()
                .map(i -> quotaMapper.findLast(i, dateStart)).filter(Objects::nonNull)
                .collect(Collectors.toMap(Quota::getCode, i -> i));
        return items.parallelStream().map(i -> itemQuotaManager.calculate(
                i,
                dateStart,
                Quota::getDate,
                () -> listMap.get(i.getCode()),
                () -> quotaMap.get(i.getCode()),
                funcMap.get(i.getType()),
                funcMap.get(i.getType()),
                funcMap.get(i.getType()))).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Item> items() {
        List<Item> items = new ArrayList<>();
        List<String> codes = DefaultQuota.codes();
        List<String> fcs = QuotaField.codes();
        for (String code : codes) {
            for (String fc : fcs) {
                Item item = new Item();
                item.setCode(code);
                item.setType(fc);
                item.setName(DefaultQuota.codeToName(code) + "-" + QuotaField.codeToName(fc));
                items.add(item);
            }
        }
        return items;
    }

    private Map<String, Function<Quota, Double>> funcMap() {
        Map<String, Function<Quota, Double>> funcMap = new HashMap<>();
        funcMap.put(QuotaField.JG.getCode(), Quota::getPrice);
        funcMap.put(QuotaField.SY.getCode(), Quota::getSyl);
        funcMap.put(QuotaField.GS.getCode(), Quota::getCount);
        funcMap.put(QuotaField.SZ.getCode(), Quota::getSzZ);
        funcMap.put(QuotaField.SZ_LT.getCode(), Quota::getSzLt);
        funcMap.put(QuotaField.GB.getCode(), Quota::getGbZ);
        funcMap.put(QuotaField.GB_LT.getCode(), Quota::getGbLt);
        return funcMap;
    }


}
