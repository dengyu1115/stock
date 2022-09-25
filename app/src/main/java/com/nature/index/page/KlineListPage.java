package com.nature.index.page;

import com.nature.base.manager.BaseKlineManager;
import com.nature.base.page.BaseKlineListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.KlineManager;

@PageView(name = "K线-整合", group = "指数", col = 1, row = 4)
public class KlineListPage extends BaseKlineListPage {

    @Injection
    private KlineManager klineManager;

    @Override
    protected BaseKlineManager manager() {
        return this.klineManager;
    }

}
