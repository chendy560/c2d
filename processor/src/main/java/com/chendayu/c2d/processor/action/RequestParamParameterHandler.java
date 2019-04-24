package com.chendayu.c2d.processor.action;

import com.chendayu.c2d.processor.extract.DeclarationExtractor;
import com.chendayu.c2d.processor.model.Declaration;
import com.chendayu.c2d.processor.model.Property;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import java.util.List;

import static com.chendayu.c2d.processor.Utils.findName;

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
        String displayName = findName(element, requestParam.value(), requestParam.name());

        Declaration declaration = declarationExtractor.extract(element);
        if (isSimpleDeclaration(declaration) || isSimpleArray(declaration)) {
            List<String> description = action.findParameterDescription(parameterName);
            action.addUrlParameter(new Property(displayName, description, declaration));
        }
        return true;
    }
}
