package com.nature.index.page;

import com.nature.base.manager.BaseGroupManager;
import com.nature.base.page.BaseGroupListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.GroupManager;

@PageView(name = "分组", group = "指数", col = 2, row = 1)
public class GroupListPage extends BaseGroupListPage {
    @Injection
    private GroupManager groupManager;

    @Override
    protected BaseGroupManager manager() {
        return this.groupManager;
    }


}
