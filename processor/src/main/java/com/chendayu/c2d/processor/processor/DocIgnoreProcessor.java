package com.chendayu.c2d.processor.processor;

import com.chendayu.c2d.processor.DocIgnore;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class DocIgnoreProcessor extends AbstractNestedDeclarationPostProcessor {

    public DocIgnoreProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
        for (Property property : nestedDeclaration.allProperties()) {
            if (shouldIgnore(property)) {
                property.setIgnored(true);
            }
        }
    }

    private boolean shouldIgnore(Property property) {
        ExecutableElement getter = property.getGetter();
        if (getter != null) {
            return shouldIgnore(getter);
        }

        ExecutableElement setter = property.getSetter();
        if (setter != null) {
            return shouldIgnore(setter);
        }

        VariableElement field = property.getField();
        if (field != null) {
            return shouldIgnore(field);
        }

        return false;
    }

    private boolean shouldIgnore(Element element) {
        DocIgnore docIgnore = element.getAnnotation(DocIgnore.class);
        if (docIgnore == null) {
            return false;
        }
        return docIgnore.value();
    }
}
