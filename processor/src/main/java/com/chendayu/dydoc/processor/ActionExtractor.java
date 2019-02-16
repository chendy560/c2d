package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.chendayu.dydoc.processor.Utils.findName;
import static com.chendayu.dydoc.processor.Utils.findRequestMapping;

public class ActionExtractor extends InfoExtractor {

    private final ParameterExtractor parameterExtractor;

    public ActionExtractor(ProcessingEnvironment processEnv, ApiInfoStore store) {
        super(processEnv, store);
        this.parameterExtractor = new ParameterExtractor(processEnv, store);
    }

    public Action findAction(ExecutableElement element) {

        GetMapping getMapping = element.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return getAction(element, findRequestMapping(getMapping.value(), getMapping.path()), HttpMethod.GET);
        }

        PostMapping postMapping = element.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return getAction(element, findRequestMapping(postMapping.value(), postMapping.params()), HttpMethod.POST);
        }

        return null;
    }

    private Action getAction(ExecutableElement element, String path, HttpMethod method) {

        String actionName = element.getSimpleName().toString();
        String docCommentString = elementUtils.getDocComment(element);
        DocComment docComment = DocComment.create(docCommentString);

        Action action = new Action(actionName, docComment.getDescription());
        action.setPath(path);
        action.setMethod(method);

        List<? extends VariableElement> parameters = element.getParameters();
        for (VariableElement parameterElement : parameters) {

            RequestParam requestParam = parameterElement.getAnnotation(RequestParam.class);
            if (requestParam != null) {

                String parameterName = findName(parameterElement.getSimpleName().toString(),
                        requestParam.value(), requestParam.name());
                List<String> description = docComment.getParam(parameterElement);
                action.addPathVariable(parameterExtractor.getParameter(parameterName, description, parameterElement));
                continue;
            }

            PathVariable pathVariable = parameterElement.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String name = findName(parameterElement.getSimpleName().toString(),
                        pathVariable.value(), pathVariable.name());
                List<String> description = docComment.getParam(parameterElement);
                action.addUrlParameter(parameterExtractor.getParameter(name, description, parameterElement));
            }

            RequestBody requestBody = parameterElement.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                String name = parameterElement.getSimpleName().toString();
                List<String> description = docComment.getParam(parameterElement);
                action.setRequestBody(parameterExtractor.getParameter(name, description, parameterElement));
            }

        }

        TypeMirror returnType = element.getReturnType();
        if (returnType.getKind() == TypeKind.DECLARED) {
            List<String> returnComment = docComment.getReturn();
            Parameter parameter = parameterExtractor.getParameter("", returnComment, returnType);
            action.setResponseBody(parameter);
        }

        return action;
    }


}
