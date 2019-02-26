package com.chendayu.dydoc.processor;

import javax.annotation.processing.ProcessingEnvironment;

public interface ObjectDeclarationPostProcessor {

    void setProcessionEnv(ProcessingEnvironment processingEnv);

    void process(ObjectDeclaration objectDeclaration);
}
