package com.nature.stock.common.util;

import java.util.List;
import java.util.function.Function;

/**
 * 计算工具类
 * @author nature
 * @version 1.0.0
 * @since 2019/11/6 23:14
 */
public class CalUtil {

    /**
     * 极大值
     * @param data     data
     * @param len      范围
     * @param function function
     * @param <T>      T
     * @return double
     */
    public static <T> double max(List<T> data, int len, Function<T, Double> function) {
        int size = data.size();
        if (size < len) throw new RuntimeException("size is little than " + len);
        List<T> list = data.subList(size - len, size);
        return max(list, function);
    }

    /**
     * 极大值
     * @param list     data
     * @param function function
     * @param <T>      T
     * @return double
     */
    public static <T> Double max(List<T> list, Function<T, Double> function) {
        return list.stream().map(function).max(Double::compareTo).orElse(function.apply(list.get(0)));
    }

    /**
     * 极小值
     * @param data     data
     * @param len      范围
     * @param function function
     * @param <T>      T
     * @return double
     */
    public static <T> double min(List<T> data, int len, Function<T, Double> function) {
        int size = data.size();
        if (size < len) throw new RuntimeException("size is little than " + len);
        List<T> list = data.subList(size - len, size);
        return min(list, function);
    }

    /**
     * 极小值
     * @param list     data
     * @param function function
     * @param <T>      T
     * @return double
     */
    public static <T> Double min(List<T> list, Function<T, Double> function) {
        return list.stream().map(function).min(Double::compareTo).orElse(function.apply(list.get(0)));
    }

    /**
     * 计算最大涨幅
     * @param list list
     * @param func func
     * @param <T>  T
     * @return double
     */
    public static <T> double maxUp(List<T> list, Function<T, Double> func) {
        if (list.size() < 2) {
            return 0;
        }
        double min = func.apply(list.get(0)), rate = 0;
        int idxMin = 0;
        for (int i = 1; i < list.size() - 1; i++) {
            double pre = func.apply(list.get(i - 1));
            double curr = func.apply(list.get(i));
            double next = func.apply(list.get(i + 1));
            if (curr >= pre && curr > next) {
                for (int j = idxMin; j < i; j++) {
                    double minTemp = func.apply(list.get(j));
                    if (minTemp < min) {
                        min = minTemp;
                        idxMin = j;
                    }
                }
                double rateTemp = (curr - min) / min;
                if (rateTemp > rate) {
                    rate = rateTemp;
                }
            }
        }
        int i = list.size() - 1;
        double pre = func.apply(list.get(i - 1));
        double curr = func.apply(list.get(i));
        if (curr > pre) {
            for (int j = idxMin; j < i; j++) {
                double minTemp = func.apply(list.get(j));
                if (minTemp < min) {
                    min = minTemp;
                }
            }
            double rateTemp = (curr - min) / min;
            if (rateTemp > rate) {
                rate = rateTemp;
            }
        }
        return rate;
    }

    /**
     * 计算最大跌幅
     * @param list data
     * @param func func
     * @param <T>  T
     * @return double
     */
    public static <T> double maxDown(List<T> list, Function<T, Double> func) {
        if (list.size() < 2) {
            return 0;
        }
        double max = func.apply(list.get(0)), rate = 0;
        int idxMax = 0;
        for (int i = 1; i < list.size() - 1; i++) {
            double pre = func.apply(list.get(i - 1));
            double curr = func.apply(list.get(i));
            double next = func.apply(list.get(i + 1));
            if (curr <= pre && curr < next) {
                for (int j = idxMax; j < i; j++) {
                    double maxTemp = func.apply(list.get(j));
                    if (maxTemp > max) {
                        max = maxTemp;
                        idxMax = j;
                    }
                }
                double rateTemp = (curr - max) / max;
                if (rateTemp < rate) {
                    rate = rateTemp;
                }
            }
        }
        int i = list.size() - 1;
        double pre = func.apply(list.get(i - 1));
        double curr = func.apply(list.get(i));
        if (curr < pre) {
            for (int j = idxMax; j < i; j++) {
                double maxTemp = func.apply(list.get(j));
                if (maxTemp > max) {
                    max = maxTemp;
                }
            }
            double rateTemp = (curr - max) / max;
            if (rateTemp < rate) {
                rate = rateTemp;
            }
        }
        return rate;
    }
}
