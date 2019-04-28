package com.chendayu.c2d.processor.processor;

import com.chendayu.c2d.processor.declaration.ObjectDeclaration;

/**
 * 对象声明后处理器
 * 主要用途应该是增减字段
 */
public interface ObjectDeclarationPostProcessor {

    /**
     * 处理对象声明
     *
     * @param objectDeclaration 被处理的对象声明
     */
    void process(ObjectDeclaration objectDeclaration);

    /**
     * 处理器的优先级，数字越小越靠早被调用
     *
     * @return 处理器的优先级，数字越小越靠早被调用
     */
    int getOrder();
}
