package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collection;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

/**
 * 处理 lombok 的 Getter 和 Data 注解
 * 将所有的 field 作为 property 放进 declaration
 */
public class LombokProcessor extends AbstractNestedDeclarationPostProcessor {

    public LombokProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
        Collection<Property> properties = nestedDeclaration.allProperties();

        Data dataAnnotation = nestedDeclaration.getAnnotation(Data.class);
        if (dataAnnotation != null) {
            allFieldsGettable(properties);
            allFieldsSettable(properties);
            return;
        }

        Value valueAnnotation = nestedDeclaration.getAnnotation(Value.class);
        if (valueAnnotation != null) {
            allFieldsGettable(properties);
            return;
        }

        Getter getterAnnotation = nestedDeclaration.getAnnotation(Getter.class);
        if (getterAnnotation != null && getterAnnotation.value() == AccessLevel.PUBLIC) {
            allFieldsGettable(properties);
        }

        Setter setterAnnotation = nestedDeclaration.getAnnotation(Setter.class);
        if (setterAnnotation != null && setterAnnotation.value() == AccessLevel.PUBLIC) {
            allFieldsSettable(properties);
        }
    }

    private void allFieldsGettable(Collection<Property> properties) {
        for (Property property : properties) {
            if (property.getField() != null) {
                property.setGettable(true);
            }
        }
    }

    private void allFieldsSettable(Collection<Property> properties) {
        for (Property property : properties) {
            if (property.getField() != null) {
                property.setSettable(true);
            }
        }
    }

    @Override
    public int getOrder() {
        return highestOrder();
    }
}
