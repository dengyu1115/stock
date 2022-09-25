package com.nature.stock.manager;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.manager.BaseNetManager;
import com.nature.base.mapper.BaseNetMapper;
import com.nature.base.model.Net;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.http.NetKlineHttp;
import com.nature.stock.mapper.NetMapper;
import com.nature.stock.model.Stock;

import java.util.List;

@Component
public class NetManager extends BaseNetManager<Stock> {

    @Injection
    private NetKlineHttp netKlineHttp;
    @Injection
    private NetMapper netMapper;
    @Injection
    private StockManager stockManager;

    @Override
    protected BaseNetMapper mapper() {
        return this.netMapper;
    }

    @Override
    protected BaseItemManager<Stock> itemManager() {
        return this.stockManager;
    }

    @Override
    protected List<Net> listFromHttp(String code, String market, String start, String end) {
        return netKlineHttp.list(code, market, start, end);
    }
}
