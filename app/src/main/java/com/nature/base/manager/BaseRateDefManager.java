package com.nature.base.manager;

import com.nature.base.mapper.BaseRateDefMapper;
import com.nature.base.model.RateDef;

import java.util.List;

public abstract class BaseRateDefManager {

    public int merge(RateDef d) {
        return this.mapper().merge(d);
    }

    public int delete(String type, String code) {
        return this.mapper().delete(type, code);
    }

    public List<RateDef> list(String type) {
        return this.mapper().list(type);
    }

    public RateDef find(String type, String code) {
        return this.mapper().find(type, code);
    }

    protected abstract BaseRateDefMapper mapper();
}
