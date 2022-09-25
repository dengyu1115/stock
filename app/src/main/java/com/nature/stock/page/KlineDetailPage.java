package com.nature.stock.page;

import com.nature.base.manager.BaseKlineManager;
import com.nature.base.page.BaseKlineDetailPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.KlineManager;

@PageView(name = "K线明细", group = "股票", col = 0, row = 0)
public class KlineDetailPage extends BaseKlineDetailPage {

    @Injection
    private KlineManager klineManager;

    @Override
    protected BaseKlineManager manager() {
        return this.klineManager;
    }

}
