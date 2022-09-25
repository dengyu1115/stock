package com.nature.stock.manager;

import com.nature.base.manager.BaseKlineManager;
import com.nature.base.mapper.BaseKlineMapper;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.KlineMapper;

@Component
public class KlineManager extends BaseKlineManager {

    @Injection
    private KlineMapper klineMapper;


    @Override
    protected BaseKlineMapper mapper() {
        return this.klineMapper;
    }
}
