package com.nature.common.ioc.holder;

import java.util.HashMap;
import java.util.Map;

/**
 * 实例持有
 * @author nature
 * @version 1.0.0
 * @since 2019/11/21 16:33
 */
public class InstanceHolder {

    /**
     * 存放实例的map
     */
    private static final Map<Class<?>, Object> map = new HashMap<>();

    /**
     * 获取实例
     * @param tClass 实例class
     * @param <T>    类型
     * @return 实例
     */
    @SuppressWarnings("all")
    public static <T> T get(Class<T> tClass) {
        Object o = map.get(tClass);
        if (o == null) synchronized (tClass) { // 单例存放
            if ((o = map.get(tClass)) == null) {
                try {
                    map.put(tClass, o = tClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) o;
    }

}
