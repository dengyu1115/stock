package com.nature.stock.item.manager;

import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.ioc.annotation.Injection;
import com.nature.stock.item.http.BaseStockHttp;
import com.nature.stock.item.http.BjStockHttp;
import com.nature.stock.item.http.ShStockHttp;
import com.nature.stock.item.http.SzStockHttp;
import com.nature.stock.item.mapper.ItemMapper;
import com.nature.stock.item.model.Item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * item
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 11:35
 */
public class ItemManager {

    private static final int BATCH_SIZE = 200;

    @Injection
    private ItemMapper itemMapper;
    @Injection
    private SzStockHttp szStockHttp;
    @Injection
    private ShStockHttp shStockHttp;
    @Injection
    private BjStockHttp bjStockHttp;

    public int reloadAll() {
        List<Item> list = Arrays.asList(szStockHttp, shStockHttp, bjStockHttp)
                .parallelStream().map(BaseStockHttp::list).flatMap(List::stream).collect(Collectors.toList());
        itemMapper.delete();
        return this.batchMerge(list);
    }


    /**
     * 批量保存
     * @param list list
     * @return int
     */
    public int batchMerge(List<Item> list) {
        if (list == null || list.isEmpty()) return 0;
        return BaseDB.create().batchExec(list, BATCH_SIZE, itemMapper::batchMerge);
    }

    /**
     * 查询
     * @return list
     */
    public List<Item> list() {
        return this.listByType(null);
    }

    /**
     * 查询
     * @param type 类型
     * @return list
     */
    public List<Item> listByType(String type) {
        return itemMapper.listByType(type);
    }

    /**
     * 按关键字查询
     * @param keyWord keyWord
     * @return list
     */
    public List<Item> listByKeyWord(String keyWord) {
        List<Item> items = itemMapper.listByKeyWord(keyWord);
        items.sort(Comparator.comparing(Item::getCode));
        return items;
    }
}
