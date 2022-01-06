package com.nature.func.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.func.mapper.StrategyMapper;
import com.nature.func.model.Strategy;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class StrategyManager {

    @Injection
    private StrategyMapper strategyMapper;

    public int merge(Strategy d) {
        return strategyMapper.merge(d);
    }

    public List<Strategy> list() {
        return strategyMapper.list();
    }

    public int delete(String code) {
        return strategyMapper.delete(code);
    }

    public Strategy findByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        return strategyMapper.findByCode(code);
    }
}
