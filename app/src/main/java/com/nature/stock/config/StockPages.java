package com.nature.stock.config;

import com.nature.common.page.Page;
import com.nature.stock.page.ItemListPage;
import com.nature.stock.page.KlineListPage;
import com.nature.stock.page.NetListPage;
import com.nature.stock.page.PriceListPage;

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
            add(mapLeft);
        }
    };
}
