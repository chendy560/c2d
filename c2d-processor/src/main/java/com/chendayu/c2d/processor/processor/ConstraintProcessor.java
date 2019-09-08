package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

public class ConstraintProcessor extends AbstractNestedDeclarationPostProcessor {

    private List<Class<? extends Annotation>> annotationClasses = new ArrayList<>();

    public ConstraintProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
        initAnnotationClasses();
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
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

    private void initAnnotationClasses() {
        annotationClasses.add(NotNull.class);
    }

    @Override
    public int getOrder() {
        return lowestOrder();
    }
}
