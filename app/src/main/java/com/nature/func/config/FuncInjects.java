package com.nature.func.config;

import com.nature.func.manager.WorkdayManager;

public interface FuncInjects {

    Class<?>[] CLASSES = new Class<?>[]{
            WorkdayManager.class
    };
}
