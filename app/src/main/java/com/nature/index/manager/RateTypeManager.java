package com.nature.index.manager;

import com.nature.base.manager.BaseRateTypeManager;
import com.nature.base.mapper.BaseRateTypeMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.index.mapper.RateTypeMapper;

@Component
public class RateTypeManager extends BaseRateTypeManager {

    @Injection
    private RateTypeMapper rateTypeMapper;

    @Override
    protected BaseRateTypeMapper mapper() {
        return this.rateTypeMapper;
    }
}
