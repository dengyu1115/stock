package com.nature.base.manager;

import com.nature.base.mapper.BaseItemGroupMapper;
import com.nature.base.model.ItemGroup;

import java.util.List;

public abstract class BaseItemGroupManager {

    public int merge(ItemGroup i) {
        return this.mapper().merge(i);
    }

    public int delete(String group, String code, String market) {
        return this.mapper().delete(group, code, market);
    }

    public List<ItemGroup> listByGroup(String group) {
        if (group == null) {
            throw new RuntimeException("group is null");
        }
        return this.mapper().listByGroup(group);
    }

    protected abstract BaseItemGroupMapper mapper();

}
