package com.nature.base.mapper;

import com.nature.base.model.RateDef;

import java.util.List;

public interface BaseRateDefMapper {

    int merge(RateDef d);

    int delete(String type, String code);

    List<RateDef> list(String type);

    RateDef find(String type, String code);
}
