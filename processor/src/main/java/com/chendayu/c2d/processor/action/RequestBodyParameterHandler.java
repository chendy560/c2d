package com.chendayu.c2d.processor.action;

import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.property.Property;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import java.util.List;

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
        String parameterNaem = element.getSimpleName().toString();
        List<String> description = action.findParameterDescription(parameterNaem);
        Property property = new Property(description, declaration);
        action.setRequestBody(property);
        return true;
    }
}
