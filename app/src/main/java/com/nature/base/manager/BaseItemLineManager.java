package com.nature.base.manager;

import com.nature.base.model.ItemLine;

import java.util.List;

public interface BaseItemLineManager<T extends ItemLine> {

    List<T> listAsc(String code, String market, String start, String end);
}
