package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.mapper.KlineMapper;
import com.nature.stock.model.Kline;

import java.util.List;

public class KlineManager {

    @Injection
    private KlineMapper klineMapper;

    public List<Kline> listByDate(String date, String keyWord) {
        return klineMapper.listByDate(date, keyWord);
    }

    public List<Kline> list(String code, String market) {
        return klineMapper.list(code, market);
    }

    public List<Kline> list(String code, String market, String start, String end) {
        return klineMapper.list(code, market, start, end);
    }

}
