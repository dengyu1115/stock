package com.nature.stock.common.ioc.config;

import com.nature.stock.common.manager.*;
import com.nature.stock.func.manager.ListDefManager;
import com.nature.stock.func.manager.ItemQuotaManager;
import com.nature.stock.func.manager.MarkManager;
import com.nature.stock.func.manager.TargetManager;
import com.nature.stock.item.manager.*;

/**
 * 需要进行注入操作的类
 *
 * @author nature
 * @version 1.0.0
 * @since 2019/11/23 19:38
 */
public interface InjectClasses {

    Class<?>[] CLASSES = new Class[]{
            WorkdayManager.class,
            TaskManager.class,
            KlineManager.class,
            ItemManager.class,
            GroupManager.class,
            ItemGroupManager.class,
            QuotaManager.class,
            LineManager.class,
            StrategyManager.class,
            DefinitionManager.class,
            ListDefManager.class,
            ItemQuotaManager.class,
            MarkManager.class,
            TargetManager.class,
            TaskInfoManager.class
    };
}
