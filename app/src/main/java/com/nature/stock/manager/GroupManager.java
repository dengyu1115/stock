package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.GroupMapper;
import com.nature.stock.model.Group;

import java.util.List;

@Component
public class GroupManager {

    @Injection
    private GroupMapper groupMapper;

    public int merge(Group group) {
        return groupMapper.merge(group);
    }

    public List<Group> list() {
        return groupMapper.list();
    }

    public int delete(String code) {
        return groupMapper.delete(code);
    }

    public Group findByCode(String code) {
        return groupMapper.findByCode(code);
    }
}
