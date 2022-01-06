package com.nature.item.manager;

import com.nature.common.enums.ItemType;
import com.nature.common.ioc.annotation.Injection;
import com.nature.item.mapper.ItemGroupMapper;
import com.nature.item.model.Item;
import com.nature.item.model.ItemGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目分组
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 11:35
 */
public class ItemGroupManager {

    private static final int BATCH_SIZE = 200;

    @Injection
    private ItemGroupMapper itemGroupMapper;

    /**
     * 批量保存
     * @param i i
     * @return int
     */
    public int merge(ItemGroup i) {
        return itemGroupMapper.merge(i);
    }

    /**
     * 查询
     * @return list
     */
    public List<Item> listItem(String group) {
        return this.listItem(group, null);
    }

    /**
     * 查询
     * @return list
     */
    public List<Item> listItem(String group, String keyWord) {
        List<ItemGroup> list = itemGroupMapper.list(group, keyWord);
        return list.stream().map(i -> {
            Item item = new Item();
            item.setCode(i.getCode());
            item.setMarket(i.getMarket());
            item.setName(i.getName());
            item.setType(i.getType());
            return item;
        }).sorted(Comparator.comparing(Item::getCode)).collect(Collectors.toList());
    }

    public List<Item> listAllFunds() {
        return this.listItem(ItemType.FUND.getCode());
    }

    public int delete(String group, String code, String market) {
        return itemGroupMapper.delete(group, code, market);
    }

    public List<ItemGroup> listByGroups(List<String> groups) {
        if (groups == null || groups.isEmpty()) return new ArrayList<>();
        return itemGroupMapper.listByGroups(groups);
    }
}
