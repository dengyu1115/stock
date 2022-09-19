package com.nature.stock.page;

import com.nature.base.manager.BaseRateTypeManager;
import com.nature.base.page.BaseRateDefListPage;
import com.nature.base.page.BaseRateTypeListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.stock.manager.RateTypeManager;

@PageView(name = "涨幅配置", group = "股票", col = 3, row = 1)
public class RateTypeListPage extends BaseRateTypeListPage {

    @Injection
    private RateTypeManager rateTypeManager;

    @Override
    protected BaseRateTypeManager manager() {
        return this.rateTypeManager;
    }

    @Override
    protected Class<? extends BaseRateDefListPage> jumpPage() {
        return RateDefListPage.class;
    }

}
