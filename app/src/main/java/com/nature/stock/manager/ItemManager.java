package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.http.BaseStockHttp;
import com.nature.stock.http.BjStockHttp;
import com.nature.stock.http.ShStockHttp;
import com.nature.stock.http.SzStockHttp;
import com.nature.stock.mapper.ItemMapper;
import com.nature.stock.model.Item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemManager {

    @Injection
    private ItemMapper itemMapper;
    @Injection
    private SzStockHttp szStockHttp;
    @Injection
    private ShStockHttp shStockHttp;
    @Injection
    private BjStockHttp bjStockHttp;

    public int reload() {
        List<Item> list = Arrays.asList(szStockHttp, shStockHttp, bjStockHttp)
                .parallelStream().map(BaseStockHttp::list).flatMap(List::stream).collect(Collectors.toList());
        itemMapper.delete();
        return itemMapper.batchMerge(list);
    }

    public List<Item> list() {
        return itemMapper.list(null);
    }

    public List<Item> list(String keyWord) {
        return itemMapper.list(keyWord);
    }

    public List<Item> list(String group, String keyWord) {
        if (group == null) {
            throw new IllegalArgumentException("group is null");
        }
        return itemMapper.list(group, keyWord);
    }

}
