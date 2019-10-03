package com.chendayu.c2d.processor.processor;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;

/**
 * 对象声明处理器
 */
public interface NestedDeclarationProcessor extends Comparable<NestedDeclarationProcessor> {

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

    @Override
    default int compareTo(NestedDeclarationProcessor o) {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
