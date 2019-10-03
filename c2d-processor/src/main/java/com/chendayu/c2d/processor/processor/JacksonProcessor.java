package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collection;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 处理 jackson 的一些注解
 * jackson 的注解很多，功能很强，但是我很菜，所以选择我用到了什么就支持什么
 */
public class JacksonProcessor extends AbstractNestedDeclarationProcessor {

    public JacksonProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {

        Collection<Property> properties = nestedDeclaration.allProperties();

        for (Property property : properties) {
            processJacksonAnnotations(property);
        }
    }

    private void processJacksonAnnotations(Property property) {
        JsonIgnore jsonIgnore = property.getAnnotation(JsonIgnore.class);

        if (jsonIgnore != null) {
            property.setIgnored(jsonIgnore.value());
        }

        JsonProperty jsonProperty = property.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            String displayName = property.getDisplayName();
            String annotatedName = jsonProperty.value();
            if (!annotatedName.isEmpty() && !displayName.equals(annotatedName)) {
                property.setDisplayName(annotatedName);
            }
        }
    }
}
