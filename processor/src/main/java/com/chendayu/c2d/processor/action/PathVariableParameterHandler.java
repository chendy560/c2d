package com.chendayu.c2d.processor.action;

import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.property.Property;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import java.util.List;

import static com.chendayu.c2d.processor.Utils.findName;

/**
 * 路径参数处理器
 */
public class PathVariableParameterHandler extends AbstractParameterHandler {

    public PathVariableParameterHandler(ProcessingEnvironment processingEnv, DeclarationExtractor declarationExtractor) {
        super(processingEnv, declarationExtractor);
    }

    @Override
    public boolean handleParameter(Action action, VariableElement element) {

        PathVariable requestParam = element.getAnnotation(PathVariable.class);
        if (requestParam == null) {
            return false;
        }

        String parameterName = element.getSimpleName().toString();
        String displayName = findName(element, requestParam.value(), requestParam.name());

        Declaration declaration = declarationExtractor.extract(element);
        if (isSimpleDeclaration(declaration) || isSimpleArray(declaration)) {
            List<String> description = action.findParameterDescription(parameterName);
            action.addPathVariable(new Property(displayName, description, declaration));
        }
        return true;
    }
}
