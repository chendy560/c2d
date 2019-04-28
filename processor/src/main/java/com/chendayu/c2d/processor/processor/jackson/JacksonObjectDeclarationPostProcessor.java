package com.chendayu.c2d.processor.processor.jackson;

import com.chendayu.c2d.processor.declaration.ObjectDeclaration;
import com.chendayu.c2d.processor.processor.AbstractObjectDeclarationPostProcessor;
import com.chendayu.c2d.processor.property.ObjectProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 处理 jackson 的一些注解
 * jackson 的注解很多，功能很强，但是我很菜，所以选择我用到了什么就支持什么
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
        String name = property.getDisplayName();
        JsonIgnore jsonIgnore = getAnnotation(property, JsonIgnore.class);
        // jsonIgnore 是有一个默认为 true 的 value 的，感觉是一个非常迷惑的api
        if (jsonIgnore != null && jsonIgnore.value()) {
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
