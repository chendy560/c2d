package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.Warehouse;

import javax.annotation.processing.ProcessingEnvironment;

public abstract class AbstractDeclarationExtractor extends InfoExtractor implements IDeclarationExtractor {

    public AbstractDeclarationExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        super(processingEnvironment, warehouse);
    }
}
