package com.nature.common.ioc.config;

import com.nature.func.config.FuncInjects;
import com.nature.func.manager.*;
import com.nature.item.manager.*;
import com.nature.stock.config.StockInjects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface InjectClasses {

    Set<Class<?>> CLASSES = new HashSet<Class<?>>() {
        {
            addAll(Arrays.asList(
                    TaskManager.class,
                    KlineManager.class,
                    ItemManager.class,
                    GroupManager.class,
                    ItemGroupManager.class,
                    PriceNetManager.class,
                    NetManager.class,
                    QuotaManager.class,
                    LineManager.class,
                    StrategyManager.class,
                    ScaleManager.class,
                    DefinitionManager.class,
                    FundListDefManager.class,
                    FundRateManager.class,
                    ItemQuotaManager.class,
                    MarkManager.class,
                    TargetManager.class,
                    TaskInfoManager.class));
            addAll(Arrays.asList(FuncInjects.CLASSES));
            addAll(Arrays.asList(StockInjects.CLASSES));
        }
    };
}
