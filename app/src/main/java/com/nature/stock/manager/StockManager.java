package com.nature.stock.manager;

import com.nature.common.exception.Warn;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.RemoteExeUtil;
import com.nature.stock.http.*;
import com.nature.stock.mapper.StockMapper;
import com.nature.stock.model.Industry;
import com.nature.stock.model.Stock;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StockManager {

    @Injection
    private StockMapper stockMapper;
    @Injection
    private SzStockHttp szStockHttp;
    @Injection
    private ShStockHttp shStockHttp;
    @Injection
    private BjStockHttp bjStockHttp;
    @Injection
    private IndustryManager industryManager;
    @Injection
    private IndustryStockHttp industryStockHttp;

    public int reload() {
        List<Stock> list = Arrays.asList(szStockHttp, shStockHttp, bjStockHttp)
                .parallelStream().map(BaseStockHttp::list).flatMap(List::stream).collect(Collectors.toList());
        List<Industry> industries = industryManager.list();
        if (CollectionUtils.isEmpty(industries)) {
            throw new Warn("无行业数据");
        }
        List<List<Stock>> lists = RemoteExeUtil.exec(() -> industries, i -> industryStockHttp.list(i.getCode()));
        Map<String, Stock> map = lists.stream().flatMap(Collection::stream)
                .collect(Collectors.toMap(this::key, i -> i, (o, n) -> n));
        for (Stock i : list) {
            Stock stock = map.get(this.key(i));
            if (stock == null) {
                continue;
            }
            i.setIndustry(stock.getIndustry());
        }
        stockMapper.delete();
        return stockMapper.batchMerge(list);
    }

    public List<Stock> list() {
        return stockMapper.list(null, null, null);
    }

    public List<Stock> list(String exchange, String industry, String keyWord) {
        return stockMapper.list(exchange, industry, keyWord);
    }

    public List<Stock> list(String group, String keyWord) {
        if (group == null) {
            throw new IllegalArgumentException("group is null");
        }
        return stockMapper.list(group, keyWord);
    }

    private String key(Stock i) {
        return String.join(":", i.getCode(), i.getMarket());
    }

}
