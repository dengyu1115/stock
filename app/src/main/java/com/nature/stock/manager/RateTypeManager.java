package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.RateTypeMapper;
import com.nature.stock.model.RateType;

import java.util.List;

@Component
public class RateTypeManager {

    @Injection
    private RateTypeMapper rateTypeMapper;


    public int merge(RateType d) {
        RateType exist = rateTypeMapper.find(d.getCode());
        if (exist != null) {
            exist.setTitle(d.getTitle());
        }
        return rateTypeMapper.merge(d);
    }

    public int delete(String code) {
        return rateTypeMapper.delete(code);
    }

    public List<RateType> list() {
        return rateTypeMapper.list();
    }

    public RateType find(String code) {
        return rateTypeMapper.find(code);
    }
}
