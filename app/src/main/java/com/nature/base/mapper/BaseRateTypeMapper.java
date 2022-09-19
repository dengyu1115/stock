package com.nature.base.mapper;

import com.nature.base.model.RateType;

import java.util.List;

public interface BaseRateTypeMapper {

    int merge(RateType d);

    int delete(String code);

    RateType find(String code);

    List<RateType> list();
}
