package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.RateDefMapper;
import com.nature.stock.model.RateDef;

import java.util.List;

@Component
public class RateDefManager {

    @Injection
    private RateDefMapper rateDefMapper;

    public int merge(RateDef d) {
        return rateDefMapper.merge(d);
    }

    public int delete(String type, String code) {
        return rateDefMapper.delete(type, code);
    }

    public List<RateDef> list(String type) {
        return rateDefMapper.list(type);
    }

    public RateDef find(String type, String code) {
        return rateDefMapper.find(type, code);
    }
}
