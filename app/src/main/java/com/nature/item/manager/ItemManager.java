package com.nature.item.manager;

import com.nature.common.db.BaseDB;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.annotation.Injection;
import com.nature.item.http.BaseStockHttp;
import com.nature.item.http.BjStockHttp;
import com.nature.item.http.ShStockHttp;
import com.nature.item.http.SzStockHttp;
import com.nature.item.mapper.ItemMapper;
import com.nature.item.model.Item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public int reload(String type) {
        if (ItemType.STOCK.getCode().equals(type)) {
            return this.reloadStock();
        }
        return 0;
    }

    public int reloadStock() {
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

    public List<Item> list(String keyWord) {
        List<Item> items = itemMapper.listByKeyWord(keyWord);
        items.sort(Comparator.comparing(Item::getCode));
        return items;
    }

    public List<Item> list(String type, String keyWord) {
        List<Item> items = itemMapper.list(type, keyWord);
        items.sort(Comparator.comparing(Item::getCode));
        return items;
    }
}
