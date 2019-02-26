package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.chendayu.dydoc.processor.Utils.findName;
import static com.chendayu.dydoc.processor.Utils.findRequestMapping;

public class ActionExtractor extends InfoExtractor {

    private final DeclarationExtractor declarationExtractor;

    public ActionExtractor(ProcessingEnvironment toolbox, Warehouse warehouse) {
        super(toolbox, warehouse);
        this.declarationExtractor = new DeclarationExtractor(toolbox, warehouse);
    }

    public Action findAction(ExecutableElement element) {

        GetMapping getMapping = element.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            String requestMapping = findRequestMapping(getMapping.value(), getMapping.path());
            return createAction(element, requestMapping, HttpMethod.GET);
        }

        PostMapping postMapping = element.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            String requestMapping = findRequestMapping(postMapping.value(), postMapping.params());
            return createAction(element, requestMapping, HttpMethod.POST);
        }

        return null;
    }

    private Action createAction(ExecutableElement element, String path, HttpMethod method) {

        String actionName = element.getSimpleName().toString();
        String docCommentString = elementUtils.getDocComment(element);
        DocComment docComment = DocComment.create(docCommentString);

        Action action = new Action(actionName, docComment.getDescription());
        action.setPath(path);
        action.setMethod(method);

        List<? extends VariableElement> parameters = element.getParameters();
        for (VariableElement parameterElement : parameters) {
            handleParameter(parameterElement, action, docComment);
        }

        TypeMirror returnType = element.getReturnType();
        List<String> returnComment = docComment.getReturn();
        Declaration declaration = declarationExtractor.extractAndSave(returnType);
        Property responseBody = new Property(returnComment, declaration);
        action.setResponseBody(responseBody);

        return action;
    }

    private void handleParameter(VariableElement parameterElement, Action action, DocComment docComment) {

        RequestParam requestParam = parameterElement.getAnnotation(RequestParam.class);
        if (requestParam != null) {
            String parameterName = findName(parameterElement.getSimpleName().toString(),
                    requestParam.value(), requestParam.name());
            List<String> description = docComment.getParam(parameterElement);
            Declaration declaration = declarationExtractor.extractAndSave(parameterElement);
            action.addUrlParameter(new Property(parameterName, description, declaration));
            return;
        }

        PathVariable pathVariable = parameterElement.getAnnotation(PathVariable.class);
        if (pathVariable != null) {
            String name = findName(parameterElement.getSimpleName().toString(),
                    pathVariable.value(), pathVariable.name());
            List<String> description = docComment.getParam(parameterElement);
            Declaration declaration = declarationExtractor.extractAndSave(parameterElement);
            action.addPathVariable(new Property(name, description, declaration));
            return;
        }

        RequestBody requestBody = parameterElement.getAnnotation(RequestBody.class);
        if (requestBody != null) {
            List<String> description = docComment.getParam(parameterElement);
            Declaration declaration = declarationExtractor.extractAndSave(parameterElement);
            Property property = new Property(description, declaration);
            action.setRequestBody(property);
        }
    }
}
