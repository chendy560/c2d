package com.chendayu.c2d.processor.action;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.SupportedContentType;
import com.chendayu.c2d.processor.Utils;
import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.property.Property;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import static com.chendayu.c2d.processor.SupportedContentType.MULTIPART_FORM_DATA;
import static com.chendayu.c2d.processor.Utils.findRequestMapping;

public class ActionExtractor extends InfoExtractor {

    private final DeclarationExtractor declarationExtractor;

    private final List<ParameterHandler> parameterHandlers;

    public ActionExtractor(ProcessingEnvironment toolbox, Warehouse warehouse) {
        super(toolbox, warehouse);
        this.declarationExtractor = new DeclarationExtractor(toolbox, warehouse);
        this.parameterHandlers = defaultParameterHandlers();
    }

    private List<ParameterHandler> defaultParameterHandlers() {
        ArrayList<ParameterHandler> handlers = new ArrayList<>();
        handlers.add(new IgnoreParameterHandler(processingEnv));
        handlers.add(new RequestParamParameterHandler(processingEnv, declarationExtractor));
        handlers.add(new PathVariableParameterHandler(processingEnv, declarationExtractor));
        handlers.add(new RequestBodyParameterHandler(processingEnv, declarationExtractor));
        handlers.add(new NoAnnotationParameterHandler(processingEnv, declarationExtractor));
        return Collections.unmodifiableList(handlers);
    }

    /**
     * 这里并不支持大家喜闻乐见的 RequestMapping
     * 就是这么任性，谢谢
     */
    public Action findAction(ExecutableElement element) {

        GetMapping getMapping = element.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            String path = findRequestMapping(getMapping.value(), getMapping.path());
            return createAction(element, path, HttpMethod.GET,
                    Arrays.asList(getMapping.consumes()), Arrays.asList(getMapping.produces()));
        }

        PostMapping postMapping = element.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            String path = findRequestMapping(postMapping.value(), postMapping.params());
            return createAction(element, path, HttpMethod.POST,
                    Arrays.asList(postMapping.consumes()), Arrays.asList(postMapping.produces()));
        }

        DeleteMapping deleteMapping = element.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            String path = findRequestMapping(deleteMapping.value(), deleteMapping.params());
            return createAction(element, path, HttpMethod.DELETE,
                    Arrays.asList(deleteMapping.consumes()), Arrays.asList(deleteMapping.produces()));
        }

        PutMapping putMapping = element.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            String path = findRequestMapping(putMapping.value(), putMapping.params());
            return createAction(element, path, HttpMethod.PUT,
                    Arrays.asList(putMapping.consumes()), Arrays.asList(putMapping.produces()));
        }

        PatchMapping patchMapping = element.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            String path = findRequestMapping(patchMapping.value(), patchMapping.params());
            return createAction(element, path, HttpMethod.PATCH,
                    Arrays.asList(patchMapping.consumes()), Arrays.asList(patchMapping.produces()));
        }

        return null;
    }

    private Action createAction(ExecutableElement element, String path, HttpMethod method,
                                List<String> consume, List<String> produce) {

        String methodName = element.getSimpleName().toString();
        String actionName = Utils.upperCaseFirst(methodName);
        String docCommentString = elementUtils.getDocComment(element);
        DocComment docComment = DocComment.create(docCommentString);

        Action action = new Action(actionName, docComment);
        action.setPath(path);
        action.setMethod(method);

        List<? extends VariableElement> parameters = element.getParameters();
        for (VariableElement parameterElement : parameters) {
            handleParameter(parameterElement, action);
        }

        TypeMirror returnType = element.getReturnType();
        List<String> returnComment = docComment.getReturn();
        Declaration declaration = declarationExtractor.extract(returnType);
        Property responseBody = new Property(returnComment, declaration);
        action.setResponseBody(responseBody);

        confirmContentType(action, consume, produce);

        return action;
    }

    private void handleParameter(VariableElement parameterElement, Action action) {

        for (ParameterHandler parameterHandler : parameterHandlers) {
            boolean handled = parameterHandler.handleParameter(action, parameterElement);
            if (handled) {
                break;
            }
        }
    }

    private void confirmContentType(Action action, List<String> consume, List<String> produce) {
        inferRequestContentType(action, consume);
        inferResponseContentType(action, produce);
    }

    private void inferRequestContentType(Action action, List<String> consume) {
        if (containsFileParam(action)) {
            action.setRequestContentType(MULTIPART_FORM_DATA);
            return;
        }

        if (action.hasRequestBody()) {
            action.setRequestContentType(SupportedContentType.infer(consume));
        }
    }

    private void inferResponseContentType(Action action, List<String> produce) {
        if (action.hasResponseBody()) {
            action.setRequestContentType(SupportedContentType.infer(produce));
        }
    }

    private boolean containsFileParam(Action action) {
        List<Property> urlParameters = action.getUrlParameters();
        for (Property urlParameter : urlParameters) {
            if (urlParameter.getDeclaration().getType() == DeclarationType.FILE) {
                return true;
            }
        }

        return false;
    }
}
