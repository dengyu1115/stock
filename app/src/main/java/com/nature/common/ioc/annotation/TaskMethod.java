package com.nature.common.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 任务方法
 * @author nature
 * @version 1.0.0
 * @since 2020/2/27 16:07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskMethod {

    String value() default "";

    String name() default "";
}
