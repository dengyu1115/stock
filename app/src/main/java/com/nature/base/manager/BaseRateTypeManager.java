package com.nature.base.manager;

import com.nature.base.mapper.BaseRateTypeMapper;
import com.nature.base.model.RateType;

import java.util.List;

public abstract class BaseRateTypeManager {

    public int merge(RateType d) {
        RateType exist = this.mapper().find(d.getCode());
        if (exist != null) {
            exist.setTitle(d.getTitle());
        }
        return this.mapper().merge(d);
    }

    public int delete(String code) {
        return this.mapper().delete(code);
    }

    public List<RateType> list() {
        return this.mapper().list();
    }

    public RateType find(String code) {
        return this.mapper().find(code);
    }

    protected abstract BaseRateTypeMapper mapper();
}
