package com.chendayu.c2d.processor.processor;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;

/**
 * 对象声明后处理器
 * 主要用途应该是增减字段
 */
public interface NestedDeclarationPostProcessor {

    /**
     * 处理对象声明
     *
     * @param nestedDeclaration 被处理的对象声明
     */
    void process(NestedDeclaration nestedDeclaration);

    /**
     * 处理器的优先级，数字越小越靠早被调用
     *
     * @return 处理器的优先级，数字越小越靠早被调用
     */
    int getOrder();
}
