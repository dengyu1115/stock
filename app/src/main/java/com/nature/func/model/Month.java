package com.nature.func.model;

import com.nature.common.model.BaseModel;

import java.util.HashMap;
import java.util.Map;

public class Month extends BaseModel {

    private String month;

    private final Map<String, String> dates = new HashMap<>();

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDateType(String date, String type) {
        dates.put(date, type);
    }

    public String getDateType(String date) {
        return dates.get(date);
    }
}
