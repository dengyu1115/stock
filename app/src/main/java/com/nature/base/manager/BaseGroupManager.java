package com.nature.base.manager;

import com.nature.base.mapper.BaseGroupMapper;
import com.nature.base.model.Group;

import java.util.List;

public abstract class BaseGroupManager {

    public int merge(Group group) {
        return this.mapper().merge(group);
    }

    public List<Group> list() {
        return this.mapper().list();
    }

    public int delete(String code) {
        return this.mapper().delete(code);
    }

    public Group findByCode(String code) {
        return this.mapper().findByCode(code);
    }

    protected abstract BaseGroupMapper mapper();
}
