package com.nature.stock.config;

import com.nature.stock.manager.*;

public interface StockInjects {

    Class<?>[] CLASSES = new Class<?>[]{
            ItemManager.class,
            PriceManager.class,
            NetManager.class,
            KlineManager.class,
            GroupManager.class,
            ItemGroupManager.class,
            RateTypeManager.class,
            RateDefManager.class
    };
}
