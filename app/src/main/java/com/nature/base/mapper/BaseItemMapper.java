package com.nature.base.mapper;

import com.nature.base.model.Item;

import java.util.List;

public interface BaseItemMapper<T extends Item> {

    int batchMerge(List<T> list);

    int delete();

    List<T> list();

}
