package com.nature.base.mapper;

import com.nature.base.model.ItemGroup;

import java.util.List;

public interface BaseItemGroupMapper {

    int merge(ItemGroup i);

    int delete(String group, String code, String market);

    List<ItemGroup> listByGroup(String group);
}
