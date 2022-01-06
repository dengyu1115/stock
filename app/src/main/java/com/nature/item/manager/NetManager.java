package com.nature.item.manager;

import com.nature.common.db.BaseDB;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.TaskMethod;
import com.nature.func.manager.WorkdayManager;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.ExeUtil;
import com.nature.item.http.NetHttp;
import com.nature.item.mapper.NetMapper;
import com.nature.item.model.Item;
import com.nature.item.model.Net;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 净值
 * @author nature
 * @version 1.0.0
 * @since 2020/8/19 19:18
 */
public class NetManager {

    @Injection
    private NetMapper netMapper;

    @Injection
    private NetHttp netHttp;

    @Injection
    private ItemManager itemManager;

    @Injection
    private ItemGroupManager itemGroupManager;

    @Injection
    private WorkdayManager workdayManager;

    private Map<String, Net> map;

    public int reloadAll() {
        return ExeUtil.exec(netMapper::delete, itemGroupManager::listAllFunds, this::reload);
    }

    @TaskMethod(value = "003", name = "加载最新净值")
    public int loadLatest() {
        return ExeUtil.exec(() -> map = netMapper.listLast().stream().collect(Collectors.toMap(Net::getCode, i -> i)),
                itemGroupManager::listAllFunds, this::doLoad,
                () -> map = null);
    }

    private int doLoad(Item item) {
        String code = item.getCode();
        // 1.查询库里已存在的最新数据
        Net net = map.get(code);
        // 2.查询网络最新数据
        String date = this.getLastDate(net);
        List<Net> list = netHttp.listByCode(code, date);
        if (list.isEmpty()) return 0;
        // 3.数据计算累计增长
        list.sort(Comparator.comparing(Net::getDate));
        if (net == null) this.initTotal(list.get(0));
        else list.add(0, net);
        // 遍历数据计算
        for (int i = 1; i < list.size(); i++) this.calculateTotal(list.get(i), list.get(i - 1));
        // 库里已有数据移除
        if (net != null) list.remove(net);
        // 4.数据入库
        return this.batchMerge(list);
    }

    private String getLastDate(Net net) {
        return net == null ? "" : CommonUtil.addDays(net.getDate(), 1);
    }

    private void initTotal(Net net) {
        net.setNetTotal(net.getNet());
        net.setRateTotal(net.getRate());
    }

    private int reload(Item item) {
        List<Net> list = netHttp.listByCode(item.getCode());
        if (list.isEmpty()) return 0;
        list.sort(Comparator.comparing(Net::getDate));
        this.initTotal(list.get(0));
        for (int i = 1; i < list.size(); i++) this.calculateTotal(list.get(i), list.get(i - 1));
        return this.batchMerge(list);
    }

    private void calculateTotal(Net net, Net pre) {
        // <- t1=(t0+1)*(r1+1)-1 <- (r1+1)*(t0+1)=(t1+1) <- r1=((t1+1)-(t0+1))/(t0+1)
        net.setRateTotal((1 + pre.getRateTotal()) * (1 + net.getRate()) - 1);
        // <- n1 = n0*(1+r1) <- r1 = (n1-n0)/n0
        net.setNetTotal(pre.getNetTotal() * (1 + net.getRate()));
    }

    public int batchMerge(List<Net> list) {
        if (list == null || list.isEmpty()) return 0;
        return BaseDB.create().batchExec(list, 100, l -> netMapper.batchMerge(l));
    }

    public List<Net> listByCode(String code) {
        return netMapper.listByCode(code);
    }

    public List<Net> listAfter(String code, String date) {
        return netMapper.listAfter(code, date);
    }

    public List<Net> listLast(String date) {
        return netMapper.listLast(date);
    }

    public List<Net> list(String code, String dateStart, String dateEnd) {
        return netMapper.list(code, dateStart, dateEnd);
    }

    public Net findLast(String code, String date) {
        return netMapper.findLast(code, date);
    }

    public List<Net> listByDate(String date, String keyWord) {
        List<Net> list = netMapper.listByDate(date);
        Map<String, String> map = itemManager.list().stream()
                .collect(Collectors.toMap(Item::getCode, Item::getName, (o, n) -> n));
        if (keyWord != null && !keyWord.isEmpty()) {
            list = list.stream().filter(i -> {
                i.setName(map.get(i.getCode()));
                return i.getCode().contains(keyWord) || i.getName().contains(keyWord);
            }).collect(Collectors.toList());
        } else {
            for (Net i : list) i.setName(map.get(i.getCode()));
        }
        return list;
    }
}
