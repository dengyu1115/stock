package com.nature.stock.manager;

import com.nature.base.manager.BaseItemGroupManager;
import com.nature.base.mapper.BaseItemGroupMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.ItemGroupMapper;

@Component
public class ItemGroupManager extends BaseItemGroupManager {

    @Injection
    private ItemGroupMapper itemGroupMapper;

    @Override
    protected BaseItemGroupMapper mapper() {
        return this.itemGroupMapper;
    }

}
