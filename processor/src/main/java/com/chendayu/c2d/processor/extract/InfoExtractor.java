package com.chendayu.c2d.processor.extract;

import com.chendayu.c2d.processor.AbstractComponent;
import com.chendayu.c2d.processor.Warehouse;

import javax.annotation.processing.ProcessingEnvironment;

public abstract class InfoExtractor extends AbstractComponent {

    protected final Warehouse warehouse;

    public InfoExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        super(processingEnvironment);
        this.warehouse = warehouse;
    }
}
