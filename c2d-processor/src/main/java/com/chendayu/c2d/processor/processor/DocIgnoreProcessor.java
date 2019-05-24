package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;

import com.chendayu.c2d.processor.DocIgnore;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

public class DocIgnoreProcessor extends AbstractNestedDeclarationPostProcessor {

    public DocIgnoreProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
        for (Property property : nestedDeclaration.allProperties()) {
            DocIgnore docIgnore = property.getAnnotation(DocIgnore.class);
            if (docIgnore != null) {
                property.setIgnored(docIgnore.value());
            }
        }
    }
}
