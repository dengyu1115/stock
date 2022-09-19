package com.nature.stock.page;

import com.nature.base.manager.BaseRateDefManager;
import com.nature.base.page.BaseRateDefListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.RateDefManager;

@PageView(name = "涨幅定义配置", group = "股票", col = 0, row = 0)
public class RateDefListPage extends BaseRateDefListPage {

    @Injection
    private RateDefManager rateDefManager;

    @Override
    protected BaseRateDefManager manager() {
        return this.rateDefManager;
    }

}
