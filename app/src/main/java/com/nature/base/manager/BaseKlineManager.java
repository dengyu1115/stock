package com.nature.base.manager;

import com.nature.base.mapper.BaseKlineMapper;
import com.nature.base.model.Kline;

import java.util.List;

public abstract class BaseKlineManager {

    public List<Kline> listByDate(String date, String keyWord) {
        return this.mapper().listByDate(date, keyWord);
    }

    public List<Kline> list(String code, String market) {
        return this.mapper().list(code, market);
    }

    public List<Kline> list(String code, String market, String start, String end) {
        return this.mapper().list(code, market, start, end);
    }

    protected abstract BaseKlineMapper mapper();

}
