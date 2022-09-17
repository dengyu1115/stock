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
     * @param cls 实例class
     * @param <T> 类型
     * @return 实例
     */
    @SuppressWarnings("all")
    public static <T> T get(Class<T> cls) {
        Object o = map.get(cls);
        if (o == null) synchronized (cls) { // 单例存放
            if ((o = map.get(cls)) == null) {
                try {
                    map.put(cls, o = cls.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) o;
    }

}
