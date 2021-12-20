package com.nature.stock.common.util;

import java.util.List;
import java.util.function.Function;

public class RateCalculator {

    public static <T> double max(List<T> list, Function<T, Double> func) {
        return new Calculator<T>(list, func, GATHER).cal();
    }


    public static <T> double min(List<T> list, Function<T, Double> func) {
        return new Calculator<T>(list, func, LESS).cal();
    }

    private static class Calculator<T> {

        private final List<T> list;

        private final Function<T, Double> func;

        private final DoCompare compare;

        private Calculator(List<T> list, Function<T, Double> func, DoCompare compare) {
            this.list = list;
            this.func = func;
            this.compare = compare;
        }

        private double cal() {
            if (list.size() < 2) {
                return 0;
            }
            double rate = 0, pre = func.apply(list.get(0)), curr = func.apply(list.get(1)), mark = pre;
            for (int i = 2; i < list.size(); i++) {
                double next = func.apply(list.get(i));
                if ((pre == curr || this.compare.apply(pre, curr))
                        && this.compare.apply(next, curr) && this.compare.apply(mark, curr)) {
                    mark = curr;
                } else if ((pre == curr || this.compare.apply(curr, pre)) && this.compare.apply(curr, next)) {
                    double rateTemp = (curr - mark) / mark;
                    if (this.compare.apply(rateTemp, rate)) {
                        rate = rateTemp;
                    }
                }
                pre = curr;
                curr = next;
            }
            if (this.compare.apply(curr, pre)) {
                double rateTemp = (curr - mark) / mark;
                if (this.compare.apply(rateTemp, rate)) {
                    rate = rateTemp;
                }
            }
            return rate;
        }
    }

    private static final DoCompare GATHER = (a, b) -> a > b;

    private static final DoCompare LESS = (a, b) -> a < b;

    @FunctionalInterface
    private interface DoCompare {
        boolean apply(double a, double b);
    }
}
