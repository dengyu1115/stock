package com.nature.index.manager;

import com.nature.base.manager.BaseGroupManager;
import com.nature.base.mapper.BaseGroupMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.index.mapper.GroupMapper;

@Component
public class GroupManager extends BaseGroupManager {

    @Injection
    private GroupMapper groupMapper;

    @Override
    protected BaseGroupMapper mapper() {
        return this.groupMapper;
    }
}
