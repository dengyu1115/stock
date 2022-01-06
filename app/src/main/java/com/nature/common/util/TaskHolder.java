package com.nature.common.util;

import com.nature.func.model.TaskInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务持有
 * @author nature
 * @version 1.0.0
 * @since 2020/2/27 16:21
 */
public class TaskHolder {

    /**
     * 方法实例的map
     */
    private static final Map<String, Method> methods = new HashMap<>();
    private static final Map<String, Object> instances = new HashMap<>();
    private static final List<TaskInfo> tasks = new ArrayList<>();

    /**
     * 任务持有
     * @param code code
     */
    public static void invoke(String code) {
        try {
            methods.get(code).invoke(instances.get(code));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void put(String code, Method m, Object o) {
        methods.put(code, m);
        instances.put(code, o);
    }

    public static void add(String code, String name) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setCode(code);
        taskInfo.setName(name);
        tasks.add(taskInfo);
    }

    public static List<TaskInfo> listAll() {
        return tasks;
    }
}
