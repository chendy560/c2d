package com.chendayu.c2d.processor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 处理 jackson 的一些注解
 */
public class JacksonObjectDeclarationPostProcessor extends AbstractObjectDeclarationPostProcessor {

    public JacksonObjectDeclarationPostProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(ObjectDeclaration objectDeclaration) {

        List<ObjectProperty> properties = objectDeclaration.copyProperties();

        for (ObjectProperty objectProperty : properties) {
            processJacksonAnnotations(objectProperty, objectDeclaration);
        }
    }

    private void processJacksonAnnotations(ObjectProperty property, ObjectDeclaration declaration) {
        String name = property.getName();
        JsonIgnore jsonIgnore = getAnnotation(property, JsonIgnore.class);
        if (jsonIgnore != null) {
            declaration.removeProperty(name);
            return;
        }

        JsonProperty jsonProperty = getAnnotation(property, JsonProperty.class);
        if (jsonProperty != null) {
            String annotatedName = jsonProperty.value();
            if (!annotatedName.isEmpty() && !name.equals(annotatedName)) {
                declaration.renameProperty(name, annotatedName);
            }
        }
    }

    private <T extends Annotation> T getAnnotation(ObjectProperty property, Class<T> clazz) {
        ExecutableElement getter = property.getGetter();
        if (getter != null) {
            T annotation = getter.getAnnotation(clazz);
            if (annotation != null) {
                return annotation;
            }
        }

        VariableElement field = property.getField();
        if (field != null) {
            T annotation = field.getAnnotation(clazz);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    @Override
    public int getOrder() {
        return lowestOrder();
    }
}
