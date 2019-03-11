package com.chendayu.c2d.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 简单实现一下，把常用api弄进来
 */
public abstract class AbstractObjectDeclarationPostProcessor implements ObjectDeclarationPostProcessor {

    protected final ProcessingEnvironment processingEnv;

    protected final Elements elementUtils;

    protected final Types typeUtils;

    protected final Messager messager;

    public AbstractObjectDeclarationPostProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.messager = processingEnv.getMessager();
    }

    /**
     * 最高的优先级
     *
     * @return 最高的优先级
     */
    protected static int highestOrder() {
        return Integer.MIN_VALUE;
    }

    protected static int normalOrder() {
        return 0;
    }

    /**
     * 最低的优先级
     *
     * @return 最低的优先级
     */
    protected static int lowestOrder() {
        return Integer.MAX_VALUE;
    }
}
