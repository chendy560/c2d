package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;

import com.chendayu.c2d.processor.AbstractComponent;

/**
 * 简单实现一下，把常用api弄进来
 */
public abstract class AbstractNestedDeclarationPostProcessor extends AbstractComponent implements NestedDeclarationPostProcessor {

    public static final int NORMAL_ORDER = 0;

    public AbstractNestedDeclarationPostProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    /**
     * 最高的优先级
     *
     * @return 最高的优先级
     */
    protected static int highestOrder() {
        return Integer.MIN_VALUE;
    }

    /**
     * 默认的优先级
     *
     * @return 默认的优先级
     */
    protected static int normalOrder() {
        return NORMAL_ORDER;
    }

    /**
     * 最低的优先级
     *
     * @return 最低的优先级
     */
    protected static int lowestOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * 默认使用默认优先级
     *
     * @return 默认优先级
     */
    @Override
    public int getOrder() {
        return normalOrder();
    }
}
