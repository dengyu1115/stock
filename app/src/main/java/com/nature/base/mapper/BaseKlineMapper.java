package com.nature.base.mapper;

import com.nature.base.model.Kline;

import java.util.List;

public interface BaseKlineMapper {


    List<Kline> list(String code, String market);

    List<Kline> list(String code, String market, String start, String end);

    List<Kline> listByDate(String date, String keyword);

}
