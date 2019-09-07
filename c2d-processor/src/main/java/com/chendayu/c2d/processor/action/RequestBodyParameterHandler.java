package com.chendayu.c2d.processor.action;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.property.Property;

import org.springframework.web.bind.annotation.RequestBody;

/**
 * body参数处理
 */
public class RequestBodyParameterHandler extends AbstractParameterHandler {

    public RequestBodyParameterHandler(ProcessingEnvironment processingEnv, DeclarationExtractor declarationExtractor) {
        super(processingEnv, declarationExtractor);
    }

    @Override
    public boolean handleParameter(Action action, VariableElement element) {
        RequestBody requestBody = element.getAnnotation(RequestBody.class);
        if (requestBody == null) {
            return false;
        }

        Declaration declaration = declarationExtractor.extract(element);
        String parameterName = element.getSimpleName().toString();
        String description = action.findParameterDescription(parameterName);
        Property property = new Property(null, description, declaration);
        action.setRequestBody(property);
        return true;
    }
}
