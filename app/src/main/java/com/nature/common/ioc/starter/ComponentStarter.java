package com.nature.common.ioc.starter;

import android.content.Context;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.ioc.annotation.TaskMethod;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.ioc.holder.PageHolder;
import com.nature.common.util.TaskHolder;
import dalvik.system.DexFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class ComponentStarter {

    private static ComponentStarter instance;

    private boolean ran;

    private ComponentStarter() {
    }

    public static ComponentStarter getInstance() {
        if (instance == null) {
            synchronized (ComponentStarter.class) {
                if (instance == null) {
                    instance = new ComponentStarter();
                }
            }
        }
        return instance;
    }

    public synchronized void start(Context ctx) {
        if (ran) {
            return;
        }
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                throw new RuntimeException("no context class loader");
            }
            DexFile home = new DexFile(ctx.getPackageResourcePath());
            Enumeration<String> entries = home.entries();
            while (entries.hasMoreElements()) {
                String element = entries.nextElement();
                if (!element.startsWith("com.nature") || element.contains("$$ExternalSyntheticLambda")
                        || !this.isNeededPath(element)) {
                    continue;
                }
                Class<?> cls = loader.loadClass(element);
                if (!this.isNeedType(cls)) {
                    continue;
                }
                Component component = cls.getAnnotation(Component.class);
                if (component != null) {
                    this.inject(cls, InstanceHolder.get(cls));
                }
                PageView pageView = cls.getAnnotation(PageView.class);
                if (pageView != null) {
                    this.inject(cls, InstanceHolder.get(cls));
                    PageHolder.register(cls, pageView);
                }
            }
            ran = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void inject(Class<?> cls, Object o) {
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            TaskMethod annotation = m.getAnnotation(TaskMethod.class);
            if (annotation == null) {
                continue;
            }
            TaskHolder.put(annotation.value(), m, o);
            TaskHolder.add(annotation.value(), annotation.name());
        }

        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Injection.class)) {
                Class<?> type = field.getType();
                field.setAccessible(true);
                try {
                    field.set(o, InstanceHolder.get(type));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Class<?> sc = cls.getSuperclass();
        if (sc != null && !sc.equals(Object.class)) {
            this.inject(sc, o);
        }
    }

    private boolean isNeededPath(String element) {
        List<String> paths = Arrays.asList(".page", ".manager", ".mapper");
        for (String path : paths) {
            if (element.contains(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNeedType(Class<?> cls) {
        int modifiers = cls.getModifiers();
        return !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && Modifier.isPublic(modifiers);
    }

}
