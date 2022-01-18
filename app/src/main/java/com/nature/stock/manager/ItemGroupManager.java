package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.ItemGroupMapper;
import com.nature.stock.model.ItemGroup;

public class ItemGroupManager {

    @Injection
    private ItemGroupMapper itemGroupMapper;

    public int merge(ItemGroup i) {
        return itemGroupMapper.merge(i);
    }

    public int delete(String group, String code, String market) {
        return itemGroupMapper.delete(group, code, market);
    }

}
