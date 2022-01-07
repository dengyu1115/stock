package com.nature.func.config;

import com.nature.common.page.Page;
import com.nature.func.page.WorkdayPage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface FuncPages {

    List<Map<String, Class<? extends Page>>> MENU = new ArrayList<Map<String, Class<? extends Page>>>() {
        {
            Map<String, Class<? extends Page>> mapLeft = new LinkedHashMap<String, Class<? extends Page>>() {
                {
                    put("工作日", WorkdayPage.class);
                }
            };
            add(mapLeft);
        }
    };
}
