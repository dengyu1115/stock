package com.nature.func.manager;

import com.nature.common.constant.Constant;
import com.nature.common.db.BaseDB;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.TaskMethod;
import com.nature.func.mapper.PriceNetMapper;
import com.nature.func.model.PriceNet;
import com.nature.common.util.LocalExeUtil;
import com.nature.common.util.Sorter;
import com.nature.item.http.NetHttp;
import com.nature.item.http.PriceHttp;
import com.nature.item.manager.ItemManager;
import com.nature.item.manager.KlineManager;
import com.nature.item.manager.NetManager;
import com.nature.item.manager.ScaleManager;
import com.nature.item.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 价格净值
 * @author nature
 * @version 1.0.0
 * @since 2020/7/25 18:05
 */
public class PriceNetManager {

    private static final int BATCH_SIZE = 50;
    @Injection
    private PriceHttp priceHttp;
    @Injection
    private NetHttp netHttp;
    @Injection
    private PriceNetMapper priceNetMapper;
    @Injection
    private ItemManager itemManager;
    @Injection
    private NetManager netManager;
    @Injection
    private KlineManager klineManager;
    @Injection
    private ScaleManager scaleManager;

    /**
     * 网络查询最新数据集
     * @param group   分组
     * @param date    日期
     * @param keyword 关键字
     * @return list
     */
    public List<PriceNet> listLatest(Group group, String date, String keyword) {
        // 获取当前日期
        List<PriceNet> list = this.listByDateFromHttp(date);
        // 过滤掉没有规模的数据
        Stream<PriceNet> stream = list.stream();
        if (StringUtils.isNotBlank(keyword))
            stream = stream.filter(i -> i.getCode().contains(keyword) || i.getName().contains(keyword));
        if (group != null) {
            Set<String> codes = group.getCodes();
            if (codes != null && !codes.isEmpty()) stream = stream.filter(i -> codes.contains(i.getCode()));
        }
        List<PriceNet> results = stream.sorted(Sorter.nullsLast(PriceNet::getRateDiff)).collect(Collectors.toList());
        this.appendName(results);
        return results;
    }

    /**
     * 拼接项目名称
     * @param list 数据集合
     */
    private void appendName(List<PriceNet> list) {
        List<Item> items = itemManager.list();
        Map<String, String> codeToName = items.stream().collect(Collectors.toMap(Item::getCode, Item::getName));
        list.forEach(i -> i.setName(codeToName.get(i.getCode())));
    }

    /**
     * 从网络按日期获取数据
     * @param date 日期
     * @return list
     */
    private List<PriceNet> listByDateFromHttp(String date) {
        List<PriceNet> nets = new CopyOnWriteArrayList<>(); // 存放净值数据
        List<PriceNet> prices = new CopyOnWriteArrayList<>();   // 存放价格数据
        Runnable[] tasks = new Runnable[]{  // 网络查询任务
                () -> prices.addAll(priceHttp.list(date)),
                () -> nets.addAll(netHttp.list(Constant.NET_TYPE_LOF, date)),
                () -> nets.addAll(netHttp.list(Constant.NET_TYPE_ETF, date))
        };
        IntStream.range(0, 3).parallel().forEach(i -> tasks[i].run());  // 多线程并发查询，尽量让数据获取时间一致
        // 查到的价格数据转换成map
        Map<String, PriceNet> mapPrice = prices.stream().collect(Collectors.toMap(PriceNet::getCode, i -> i));
        // 查询到的净值数据转换成map
        Map<String, PriceNet> mapNet = nets.stream().collect(Collectors.toMap(PriceNet::getCode, i -> i));
        // 价格、净值取交集
        mapPrice.keySet().retainAll(mapNet.keySet());
        // 取交集后数据属性复制
        mapPrice.values().forEach(p -> {
            PriceNet n = mapNet.get(p.getCode());
            p.setNetLast(n.getNetLast());
            p.setNetLatest(n.getNetLatest());
            this.calculateRates(p);
        });
        return new ArrayList<>(mapPrice.values());
    }

    /**
     * 查询列表数据
     * @param group   分组
     * @param date    日期
     * @param keyword 关键字
     * @return list
     */
    public List<PriceNet> list(Group group, String date, String keyword) {
        List<PriceNet> list = priceNetMapper.list(date);
        this.appendName(list);
        Stream<PriceNet> stream = list.stream().filter(i -> Math.abs(i.getRateDiff()) < 1);
        if (group != null) {
            Set<String> codes = group.getCodes();
            if (codes != null && !codes.isEmpty()) stream = stream.filter(i -> codes.contains(i.getCode()));
        }
        if (StringUtils.isNotBlank(keyword))
            stream = stream.filter(i -> i.getCode().contains(keyword) || i.getName().contains(keyword));
        return stream.sorted(Sorter.nullsLast(PriceNet::getRateDiff)).collect(Collectors.toList());
    }

    /**
     * 计算增长率等指标值
     * @param p 数据
     */
    private void calculateRates(PriceNet p) {
        p.setRateNet((p.getNetLatest() - p.getNetLast()) / p.getNetLast());
        p.setRatePrice((p.getPriceLatest() - p.getPriceLast()) / p.getPriceLast());
        p.setRateDiff((p.getPriceLatest() - p.getNetLatest()) / p.getNetLatest());
        Double scale = p.getScale();
        if (scale != null && scale > 0) p.setRateAmount(p.getAmount() / scale);
    }

    /**
     * 全部项目数据计算价值净值数据
     * @return int
     */
    @TaskMethod(value = "004", name = "计算价格净值")
    public int calculate() {
        return LocalExeUtil.exec(itemManager::list, this::calculate);
    }

    public int recalculate() {
        return LocalExeUtil.exec(priceNetMapper::delete, itemManager::list, this::calculate);
    }

    /**
     * 计算单个项目的价格净值
     * @param item 项目
     * @return int
     */
    private int calculate(Item item) {
        PriceNet pn = priceNetMapper.findLatest(item.getCode());  // 查询最新的价格净值数据
        if (pn == null) { // 不存在最新数据情况说明整体未曾计算过，取全部数据进行计算
            return this.doCalculate(item.getCode(), null, () -> netManager.listByCode(item.getCode()),
                    () -> klineManager.list(item.getCode(), item.getMarket()));
        } else {    // 存在最新数据情况，取最新数据之后的价格、净值数据进行计算
            return this.doCalculate(item.getCode(), pn, () -> netManager.listAfter(item.getCode(), pn.getDate()),
                    () -> klineManager.listAfter(item.getCode(), item.getMarket(), pn.getDate()));
        }
    }

    /**
     * 计算操作
     * @param code 编号
     * @param pn   已计算的最新净值价值数据
     * @param ns   净值数据获取
     * @param ks   价值数据获取
     * @return int
     */
    private int doCalculate(String code, PriceNet pn, Supplier<List<Net>> ns, Supplier<List<Kline>> ks) {
        List<Net> nets = ns.get();
        if (nets.isEmpty()) return 0;
        List<Kline> list = ks.get();
        if (list.isEmpty()) return 0;
        Map<String, Net> netMap = nets.stream().collect(Collectors.toMap(Net::getDate, i -> i));
        Map<String, Kline> map = list.stream().collect(Collectors.toMap(Kline::getDate, i -> i));
        Set<String> keys = new HashSet<>(netMap.keySet());
        keys.retainAll(map.keySet());   // 净值K线取交集
        List<String> dates = keys.stream().sorted(String::compareTo).collect(Collectors.toList());
        if (dates.isEmpty()) return 0;
        List<Scale> scales = scaleManager.listByCode(code);
        Map<String, Double> scaleMap = scales.stream().collect(Collectors.toMap(Scale::getDate, Scale::getAmount));
        TreeSet<String> indexes = scales.stream().map(Scale::getDate).collect(Collectors.toCollection(TreeSet::new));
        List<PriceNet> pns = new ArrayList<>();
        // 上条数据初始化
        PriceNet pre = pn == null ? this.getPre(netMap, map, dates) : pn;
        // 遍历交集数据，生成价格净值数据
        for (String d : dates) {
            Double scale = this.getScale(d, scaleMap, indexes);
            pns.add(pre = this.genPriceNet(code, netMap.get(d), map.get(d), d, pre, scale));
        }
        return this.batchMerge(pns);
    }

    private Double getScale(String date, Map<String, Double> scaleMap, TreeSet<String> indexes) {
        SortedSet<String> key = indexes.headSet(date, true);
        if (key.isEmpty()) return null;
        return scaleMap.get(indexes.last());
    }

    /**
     * 获取前一条价值净值数据
     * @param netMap 净值数据map
     * @param map    价值数据map
     * @param dates  日期集
     * @return 数据
     */
    private PriceNet getPre(Map<String, Net> netMap, Map<String, Kline> map, List<String> dates) {
        PriceNet pre = new PriceNet();
        String date = dates.get(0);
        Net net = netMap.get(date);
        Kline kline = map.get(date);
        pre.setNetLatest(net.getNet());
        pre.setPriceLatest(kline.getLatest());
        return pre;
    }

    /**
     * 单条数据价值净值计算
     * @param code  编号
     * @param net   净值
     * @param kline 价值
     * @param date  日期
     * @param pre   上一条数据
     * @param scale
     * @return 计算结果数据
     */
    private PriceNet genPriceNet(String code, Net net, Kline kline, String date, PriceNet pre, Double scale) {
        PriceNet pn = new PriceNet();
        pn.setCode(code);
        pn.setDate(date);
        pn.setAmount(kline.getAmount());
        pn.setNetLatest(net.getNet());
        pn.setPriceLatest(kline.getLatest());
        pn.setPriceHigh(kline.getHigh());
        pn.setPriceLow(kline.getLow());
        pn.setNetLast(pre.getNetLatest());
        pn.setPriceLast(pre.getPriceLatest());
        pn.setScale(scale);
        this.calculateRates(pn);    // 计算各项指标
        return pn;
    }

    /**
     * 批量保存
     * @param list 数据集合
     * @return int
     */
    public int batchSave(List<PriceNet> list) {
        if (list == null || list.isEmpty()) return 0;
        return BaseDB.create().batchExec(list, BATCH_SIZE, priceNetMapper::batchSave);
    }

    /**
     * 批量merge
     * @param list 数据集合
     * @return int
     */
    public int batchMerge(List<PriceNet> list) {
        if (list == null || list.isEmpty()) return 0;
        return BaseDB.create().batchExec(list, BATCH_SIZE, priceNetMapper::batchMerge);
    }

    /**
     * 按编号查询数据
     * @param code 编号
     * @return list
     */
    public List<PriceNet> listByCode(String code) {
        return priceNetMapper.listByCode(code);
    }

}
