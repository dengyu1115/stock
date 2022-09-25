package com.nature.index.manager;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.mapper.BaseItemMapper;
import com.nature.base.model.Item;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.index.http.ItemHttp;
import com.nature.index.mapper.ItemMapper;

import java.util.List;

@Component
public class ItemManager extends BaseItemManager<Item> {

    @Injection
    private ItemMapper itemMapper;
    @Injection
    private ItemHttp itemHttp;

    @Override
    protected BaseItemMapper<Item> mapper() {
        return this.itemMapper;
    }

    @Override
    protected List<Item> listFromHttp() {
        return itemHttp.list();
    }

}
