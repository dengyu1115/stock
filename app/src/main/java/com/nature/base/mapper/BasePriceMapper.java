package com.nature.base.mapper;

import com.nature.base.model.Price;

import java.util.List;

public interface BasePriceMapper {

    int batchMerge(List<Price> list);

    void delete();

    List<Price> list(String code, String market);

    List<Price> list(String code, String market, String start, String end);

    List<Price> listByDate(String date, String keyword);

    Price findLatest(String code, String market);

}
