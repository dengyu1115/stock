package com.nature.stock.config;

import com.nature.stock.manager.ItemManager;
import com.nature.stock.manager.KlineManager;
import com.nature.stock.manager.NetManager;
import com.nature.stock.manager.PriceManager;

public interface StockInjects {

    Class<?>[] CLASSES = new Class<?>[]{
            ItemManager.class,
            PriceManager.class,
            NetManager.class,
            KlineManager.class
    };
}
