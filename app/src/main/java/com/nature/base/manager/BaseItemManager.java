package com.nature.base.manager;

import com.nature.base.mapper.BaseItemMapper;
import com.nature.base.model.Item;

import java.util.List;

public abstract class BaseItemManager<T extends Item> {

    public int reload() {
        List<T> list = this.listFromHttp();
        this.mapper().delete();
        return this.mapper().batchMerge(list);
    }

    public List<T> list() {
        return this.mapper().list();
    }

    protected abstract BaseItemMapper<T> mapper();

    protected abstract List<T> listFromHttp();

}
