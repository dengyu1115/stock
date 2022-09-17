package com.nature.stock.manager;

import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.stock.http.IndustryHttp;
import com.nature.stock.mapper.IndustryMapper;
import com.nature.stock.model.Industry;

import java.util.List;

@Component
public class IndustryManager {

    @Injection
    private IndustryMapper industryMapper;
    @Injection
    private IndustryHttp industryHttp;

    public int reload() {
        List<Industry> list = industryHttp.list();
        industryMapper.delete();
        return industryMapper.batchMerge(list);
    }

    public List<Industry> list() {
        return industryMapper.list();
    }

}
