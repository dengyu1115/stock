package com.nature.func.config;

import com.nature.func.activity.WorkdayActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface FuncActivities {

    List<Map<String, Class<?>>> MENU = new ArrayList<Map<String, Class<?>>>() {
        {
            Map<String, Class<?>> mapLeft = new LinkedHashMap<String, Class<?>>() {
                {
                    put("工作日", WorkdayActivity.class);
                }
            };
            add(mapLeft);
        }
    };
}
