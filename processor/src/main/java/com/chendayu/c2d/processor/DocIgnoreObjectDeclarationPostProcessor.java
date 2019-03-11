package com.chendayu.c2d.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class DocIgnoreObjectDeclarationPostProcessor extends AbstractObjectDeclarationPostProcessor {

    public DocIgnoreObjectDeclarationPostProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(ObjectDeclaration objectDeclaration) {
        for (ObjectProperty property : objectDeclaration.copyProperties()) {
            if (shouldIgnore(property)) {
                objectDeclaration.removeProperty(property.getOriginName());
            }
        }
    }

    private boolean shouldIgnore(ObjectProperty property) {
        VariableElement field = property.getField();
        if (field != null && field.getAnnotation(DocIgnore.class) != null) {
            return true;
        }

        ExecutableElement getter = property.getGetter();
        if (getter != null && getter.getAnnotation(DocIgnore.class) != null) {
            return true;
        }

        return false;
    }

    @Override
    public int getOrder() {
        return normalOrder();
    }
}
