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
}
