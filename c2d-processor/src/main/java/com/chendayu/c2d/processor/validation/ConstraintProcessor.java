package com.chendayu.c2d.processor.validation;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.processor.AbstractNestedDeclarationProcessor;
import com.chendayu.c2d.processor.property.Property;

public class ConstraintProcessor extends AbstractNestedDeclarationProcessor {

    private final Collection<Class<? extends Annotation>> annotationClasses;

    public ConstraintProcessor(ProcessingEnvironment processingEnv,
                               Collection<Class<? extends Annotation>> annotationClasses) {
        super(processingEnv);
        this.annotationClasses = annotationClasses;
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
        if (this.annotationClasses.isEmpty()) {
            return;
        }
        nestedDeclaration.allProperties().forEach(this::updateConstraint);
    }

    private void updateConstraint(Property property) {
        ArrayList<Annotation> annotations = new ArrayList<>();
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            Annotation annotation = property.getAnnotation(annotationClass);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        property.setConstraintAnnotations(annotations);
    }

    @Override
    public int getOrder() {
        return lowestOrder();
    }
}
