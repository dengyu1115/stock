package com.nature.stock.common.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 执行工具
 * @author nature
 * @version 1.0.0
 * @since 2020/9/26 17:20
 */
public class ExeUtil {

    public static final int INT = 0;

    public static <I> int exec(Supplier<List<I>> list, Function<I, Integer> run) {
        List<I> items = list.get();
        Counter counter = new Counter();
        items.parallelStream().forEach(i -> doExec(run, counter, i, INT));
        return counter.get();
    }

    public static <I> int exec(Runnable init, Supplier<List<I>> list, Function<I, Integer> run) {
        if (init != null) init.run();
        List<I> items = list.get();
        Counter counter = new Counter();
        items.parallelStream().forEach(i -> doExec(run, counter, i, INT));
        return counter.get();
    }

    public static <I> int exec(Runnable init, Supplier<List<I>> list, Function<I, Integer> run, Runnable destroy) {
        init.run();
        try {
            List<I> items = list.get();
            Counter counter = new Counter();
            items.parallelStream().forEach(i -> doExec(run, counter, i, INT));
            return counter.get();
        } finally {
            destroy.run();
        }
    }

    private static <I> void doExec(Function<I, Integer> run, Counter counter, I i, int count) {
        if (count++ == 3) return;   // 失败可重试2次
        try {
            counter.count(run.apply(i));
        } catch (Exception e) {    // ignore
            doExec(run, counter, i, count);
        }
    }
}
