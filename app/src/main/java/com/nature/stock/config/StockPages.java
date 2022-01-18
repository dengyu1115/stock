package com.nature.stock.config;

import com.nature.common.page.Page;
import com.nature.stock.page.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface StockPages {

    List<Map<String, Class<? extends Page>>> MENU = new ArrayList<Map<String, Class<? extends Page>>>() {
        {
            Map<String, Class<? extends Page>> mapLeft = new LinkedHashMap<String, Class<? extends Page>>() {
                {
                    put("项目", ItemListPage.class);
                    put("K线-不复权", PriceListPage.class);
                    put("K线-复权", NetListPage.class);
                    put("K线-整合", KlineListPage.class);
                }
            };
            Map<String, Class<? extends Page>> mapCent = new LinkedHashMap<String, Class<? extends Page>>() {
                {
                    put("分组", GroupListPage.class);
                }
            };
            Map<String, Class<? extends Page>> mapRight = new LinkedHashMap<String, Class<? extends Page>>() {
                {
                    put("涨幅配置", RateTypeListPage.class);
                }
            };
            add(mapLeft);
            add(mapCent);
            add(mapRight);
        }
    };
}
