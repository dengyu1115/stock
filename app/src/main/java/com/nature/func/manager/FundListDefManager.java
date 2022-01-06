package com.nature.func.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.CommonUtil;
import com.nature.func.model.RateDef;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * kline manager
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 11:12
 */
public class FundListDefManager {

    public static final String DATE = "date";
    public static final String WEEK = "week";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String DEFINED = "defined";
    private static final Map<String, String> map = new LinkedHashMap<>();

    static {
        map.put(DATE, "日");
        map.put(WEEK, "周");
        map.put(MONTH, "月");
        map.put(YEAR, "年");
        map.put(DEFINED, "自定义");
    }

    @Injection
    private WorkdayManager workdayManager;

    public String getTypeName(String key) {
        return map.get(key);
    }

    public List<String> listAllType() {
        return new ArrayList<>(map.keySet());
    }

    public void calculateDate(RateDef def, String date) {
        String type = def.getType();
        if (DEFINED.equals(type)) {
            return;
        }
        Integer count = def.getCount();
        if (DATE.equals(type)) {
            this.cal(def, count, date, CommonUtil::addDays);
        } else if (WEEK.equals(type)) {
            this.cal(def, count, date, CommonUtil::addWeeks);
        } else if (MONTH.equals(type)) {
            this.cal(def, count, date, CommonUtil::addMonths);
        } else if (YEAR.equals(type)) {
            this.cal(def, count, date, CommonUtil::addYears);
        } else {
            throw new RuntimeException("不存在的类型：" + type);
        }
    }

    private void cal(RateDef def, Integer count, String dateEnd, BiFunction<String, Integer, String> consumer) {
        def.setDateEnd(dateEnd);
        def.setDateStart(consumer.apply(dateEnd, -count));
    }
}
