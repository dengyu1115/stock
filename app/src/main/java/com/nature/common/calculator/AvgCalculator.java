package com.nature.common.calculator;

import com.nature.stock.model.Net;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class AvgCalculator {

    private static final int WEEK = 5, MONTH = 21, SEASON = 63, YEAR = 252;

    public static void cal(List<Net> list) {
        new Multi(list).cal();
    }

    private static class Multi {

        private final List<Net> list;

        private Multi(List<Net> list) {
            this.list = list;
        }

        private void cal() {
            List<Avg> arr = Arrays.asList(new Avg(WEEK, Net::setAvgWeek), new Avg(MONTH, Net::setAvgMonth),
                    new Avg(SEASON, Net::setAvgSeason), new Avg(YEAR, Net::setAvgYear));
            for (Net net : list) {
                for (Avg avg : arr) {
                    avg.push(net);
                }
            }
        }
    }

    private static class Avg {
        private final int scale, length;
        private final Double[] arr;
        private final BiConsumer<Net, Double> handle;
        private int index;
        private double total;

        private Avg(int scale, BiConsumer<Net, Double> handle) {
            this.scale = scale;
            this.arr = new Double[this.length = scale - 1];
            this.handle = handle;
        }

        private void push(Net net) {
            Double latest = net.getLatest();
            total += latest;
            index++;
            if (index == length) {
                index = 0;
            }
            Double last = arr[index];
            if (last != null) {
                handle.accept(net, total / scale);
                total -= last;
            }
            arr[index] = latest;
        }
    }

}
