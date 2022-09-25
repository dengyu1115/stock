package com.nature.base.mapper;

import com.nature.base.model.Net;

import java.util.List;

public interface BaseNetMapper {

    int batchMerge(List<Net> list);

    void delete();

    List<Net> list(String code, String market);

    List<Net> list(String code, String market, String start, String end);

    List<Net> listBefore(String code, String market, String date, int limit);

    List<Net> listByDate(String date, String keyword);

    Net findLatest(String code, String market);

}
