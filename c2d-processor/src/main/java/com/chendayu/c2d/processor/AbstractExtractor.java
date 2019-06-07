package com.chendayu.c2d.processor;

import javax.annotation.processing.ProcessingEnvironment;

public abstract class AbstractExtractor extends AbstractComponent {

    protected final Warehouse warehouse;

    public AbstractExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        super(processingEnvironment);
        this.warehouse = warehouse;
    }
}
