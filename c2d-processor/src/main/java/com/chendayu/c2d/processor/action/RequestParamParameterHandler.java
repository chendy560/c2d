package com.chendayu.c2d.processor.action;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import java.util.List;

import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.property.Property;

import org.springframework.web.bind.annotation.RequestParam;

import static com.chendayu.c2d.processor.util.RequestParameters.findParameterName;

/**
 * requestParam 参数处理
 */
public class RequestParamParameterHandler extends AbstractParameterHandler {

    public RequestParamParameterHandler(ProcessingEnvironment processingEnv, DeclarationExtractor declarationExtractor) {
        super(processingEnv, declarationExtractor);
    }

    @Override
    public boolean handleParameter(Action action, VariableElement element) {
        RequestParam requestParam = element.getAnnotation(RequestParam.class);
        if (requestParam == null) {
            return false;
        }

        String parameterName = element.getSimpleName().toString();
        String displayName = findParameterName(parameterName, requestParam.value(), requestParam.name());

        Declaration declaration = declarationExtractor.extract(element);
        if (isSimpleDeclaration(declaration) || isSimpleArray(declaration)) {
            List<String> description = action.findParameterDescription(parameterName);
            action.addUrlParameter(new Property(displayName, description, declaration));
        }
        return true;
    }
}
