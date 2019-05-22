package com.chendayu.c2d.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于忽略内容，不对它们生成文档
 * 放置在 Controller 上：忽略这个 Controller
 * 放置在 RequestMapping 参数上，忽略参数
 * 放置在 field / getter/ setter 上，忽略 field
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface DocIgnore {

    /**
     * 仿照 {@link com.fasterxml.jackson.annotation.JsonIgnore} 的做法
     * 用于 "父类 ignore，但是子类不想 ignore" 的场景
     *
     * @return 是否忽略，默认为 true，即忽略
     */
    boolean value() default true;
}
