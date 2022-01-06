package com.nature.func.model;

import com.nature.item.model.Net;

import java.util.HashMap;
import java.util.Map;

public class FundRate extends Net {

    private final Map<String, Double> rates = new HashMap<>();

    private Double scale;

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public void putRate(String key, Double value) {
        rates.put(key, value);
    }

    public Double getRate(String key) {
        return rates.get(key);
    }
}
