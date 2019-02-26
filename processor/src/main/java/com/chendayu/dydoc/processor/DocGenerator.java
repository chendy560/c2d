package com.chendayu.dydoc.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

public class DocGenerator {

    private final Filer filer;

    private final Messager messager;

    public DocGenerator(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
    }

    public void printDoc(Warehouse warehouse) {
        //todo
    }
}
