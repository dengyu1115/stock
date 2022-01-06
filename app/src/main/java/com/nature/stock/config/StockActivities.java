package com.nature.stock.config;

import com.nature.stock.activity.ItemListActivity;
import com.nature.stock.activity.KlineListActivity;
import com.nature.stock.activity.NetListActivity;
import com.nature.stock.activity.PriceListActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface StockActivities {

    List<Map<String, Class<?>>> MENU = new ArrayList<Map<String, Class<?>>>() {
        {
            Map<String, Class<?>> mapLeft = new LinkedHashMap<String, Class<?>>() {
                {
                    put("项目", ItemListActivity.class);
                    put("K线-不复权", PriceListActivity.class);
                    put("K线-复权", NetListActivity.class);
                    put("K线-整合", KlineListActivity.class);
                }
            };
            add(mapLeft);
        }
    };
}
