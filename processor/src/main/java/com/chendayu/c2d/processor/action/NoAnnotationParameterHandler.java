package com.chendayu.c2d.processor.action;

import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.ObjectDeclaration;
import com.chendayu.c2d.processor.property.ObjectProperty;
import com.chendayu.c2d.processor.property.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.List;

/**
 * 处理没有注解的参数
 */
public class NoAnnotationParameterHandler extends AbstractParameterHandler {

    public NoAnnotationParameterHandler(ProcessingEnvironment processingEnv, DeclarationExtractor declarationExtractor) {
        super(processingEnv, declarationExtractor);
    }

    @Override
    public boolean handleParameter(Action action, VariableElement element) {
        Declaration declaration = declarationExtractor.extract(element);

        if (isSimpleDeclaration(declaration) || isSimpleArray(declaration)) {
            String parameterName = element.getSimpleName().toString();
            List<String> description = action.findParameterDescription(parameterName);
            Property property = new Property(parameterName, description,
                    declaration);
            action.addUrlParameter(property);
            return true;
        }

        if (declaration.getType() == DeclarationType.OBJECT) {
            ObjectDeclaration objectDeclaration = (ObjectDeclaration) declaration;
            Collection<ObjectProperty> properties = objectDeclaration.getProperties();
            for (ObjectProperty property : properties) {
                Declaration propertyDeclaration = property.getDeclaration();
                if (isSimpleDeclaration(propertyDeclaration) || isSimpleArray(propertyDeclaration)) {
                    action.addUrlParameter(property);
                }
            }
            return true;
        }

        return false;
    }
}
