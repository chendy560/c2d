package com.chendayu.dydoc.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class InfoExtractor {

    protected final Elements elementUtils;

    protected final Types typeUtils;

    protected final Messager messager;

    protected final Warehouse warehouse;

    public InfoExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        this.elementUtils = processingEnvironment.getElementUtils();
        this.typeUtils = processingEnvironment.getTypeUtils();
        this.messager = processingEnvironment.getMessager();
        this.warehouse = warehouse;
    }
}
