package com.chendayu.dydoc.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class AbstractObjectDeclarationPostProcessor implements ObjectDeclarationPostProcessor {

    protected ProcessingEnvironment processingEnv;

    protected Elements elementUtils;

    protected Types typeUtils;

    protected Messager messager;

    @Override
    public void setProcessionEnv(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.messager = processingEnv.getMessager();
    }
}
