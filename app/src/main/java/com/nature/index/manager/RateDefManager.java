package com.nature.index.manager;

import com.nature.base.manager.BaseRateDefManager;
import com.nature.base.mapper.BaseRateDefMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.index.mapper.RateDefMapper;

@Component
public class RateDefManager extends BaseRateDefManager {

    @Injection
    private RateDefMapper rateDefMapper;

    @Override
    protected BaseRateDefMapper mapper() {
        return this.rateDefMapper;
    }
}
