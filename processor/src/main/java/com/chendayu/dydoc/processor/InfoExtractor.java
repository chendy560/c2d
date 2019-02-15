package com.chendayu.dydoc.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class InfoExtractor {

    protected final Elements elementUtils;

    protected final Types typesUtils;

    protected final Messager messager;

    protected final ApiInfoStore store;

    public InfoExtractor(ProcessingEnvironment processingEnvironment, ApiInfoStore store) {
        this.elementUtils = processingEnvironment.getElementUtils();
        this.typesUtils = processingEnvironment.getTypeUtils();
        this.messager = processingEnvironment.getMessager();
        this.store = store;
    }
}
