package com.chendayu.dydoc.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 简单实现以下，把常用api弄进来
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

    protected final int highestOrder() {
        return Integer.MIN_VALUE;
    }
}
