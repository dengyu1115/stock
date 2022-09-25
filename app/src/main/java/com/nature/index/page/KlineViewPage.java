package com.nature.index.page;

import com.nature.base.manager.BaseKlineManager;
import com.nature.base.page.BaseKlineViewPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.index.manager.KlineManager;

@PageView(name = "K线图", group = "指数", col = 0, row = 0)
public class KlineViewPage extends BaseKlineViewPage {

    @Injection
    private KlineManager klineManager;

    @Override
    protected BaseKlineManager manager() {
        return this.klineManager;
    }
}
