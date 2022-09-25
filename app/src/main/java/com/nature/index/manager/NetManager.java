package com.nature.index.manager;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.manager.BaseNetManager;
import com.nature.base.mapper.BaseNetMapper;
import com.nature.base.model.Item;
import com.nature.base.model.Net;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.index.http.NetKlineHttp;
import com.nature.index.mapper.NetMapper;

import java.util.List;

@Component
public class NetManager extends BaseNetManager<Item> {

    @Injection
    private NetKlineHttp netKlineHttp;
    @Injection
    private NetMapper netMapper;
    @Injection
    private ItemManager itemManager;

    @Override
    protected BaseNetMapper mapper() {
        return this.netMapper;
    }

    @Override
    protected BaseItemManager<Item> itemManager() {
        return this.itemManager;
    }

    @Override
    protected List<Net> listFromHttp(String code, String market, String start, String end) {
        return netKlineHttp.list(code, market, start, end);
    }
}
