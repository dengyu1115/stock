package com.nature.stock.common.util;

import java.util.List;
import java.util.function.Function;


public class CalcUtil {

    public static <T> double maxUp(List<T> list, Function<T, Double> func) {
        if (list.size() < 2) {
            return 0;
        }
        double rate = 0;
        Double pre = func.apply(list.get(0)), curr = func.apply(list.get(1));
        Double min = pre, minTemp = null;
        for (int i = 2; i < list.size(); i++) {
            double next = func.apply(list.get(i));
            if (curr <= pre && curr < next && (minTemp == null && curr < min || minTemp != null && curr < minTemp)) {
                minTemp = curr;
            } else if (curr >= pre && curr > next) {
                if (minTemp == null) {
                    double rateTemp = (curr - min) / min;
                    if (rateTemp > rate) {
                        rate = rateTemp;
                    }
                } else {
                    double rateTemp = (curr - minTemp) / minTemp;
                    if (rateTemp > rate) {
                        rate = rateTemp;
                        min = minTemp;
                        minTemp = null;
                    }
                }
            }
            pre = curr;
            curr = next;
        }
        if (curr > pre) {
            double rateTemp;
            if (minTemp != null) {
                rateTemp = (curr - minTemp) / minTemp;
            } else {
                rateTemp = (curr - min) / min;
            }
            if (rateTemp > rate) {
                rate = rateTemp;
            }
        }
        return rate;
    }

}
