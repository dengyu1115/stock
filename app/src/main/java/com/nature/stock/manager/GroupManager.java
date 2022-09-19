package com.nature.stock.manager;

import com.nature.base.manager.BaseGroupManager;
import com.nature.base.mapper.BaseGroupMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.GroupMapper;

@Component
public class GroupManager extends BaseGroupManager {

    @Injection
    private GroupMapper groupMapper;

    @Override
    protected BaseGroupMapper mapper() {
        return this.groupMapper;
    }
}
