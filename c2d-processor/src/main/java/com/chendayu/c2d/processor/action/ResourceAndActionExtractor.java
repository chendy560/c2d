package com.chendayu.c2d.processor.action;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chendayu.c2d.processor.AbstractExtractor;
import com.chendayu.c2d.processor.DocIgnore;
import com.chendayu.c2d.processor.SupportedContentType;
import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.util.NameConversions;
import com.chendayu.c2d.processor.util.StringBuilderHolder;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.chendayu.c2d.processor.SupportedContentType.MULTIPART_FORM_DATA;
import static com.chendayu.c2d.processor.util.RequestMappings.findRequestMapping;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.TRACE;

/**
 * 从 {@link TypeElement} 里抽取 {@link Resource} 以及 {@link Action}
 */
public class ResourceAndActionExtractor extends AbstractExtractor {

    /**
     * 通常被 {@link org.springframework.stereotype.Controller} 注解的类都叫 *Controller，对吧？
     */
    private static final String CONTROLLER = "Controller";

    /**
     * 直接拿到 "Controller" 的长度，方便做 subString
     */
    private static final int CONTROLLER_LENGTH = CONTROLLER.length();

    private final DeclarationExtractor declarationExtractor;

    /**
     * 参数处理器，处理各种参数
     */
    private final List<ParameterHandler> parameterHandlers;

    public ResourceAndActionExtractor(ProcessingEnvironment processingEnv, Warehouse warehouse) {
        super(processingEnv, warehouse);
        this.declarationExtractor = new DeclarationExtractor(processingEnv, warehouse);
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
     * 抽取 {@link Resource} 和 {@link Action} 并保存到 {@link Warehouse} 中
     *
     * @param typeElement 被抽取的 {@link TypeElement}
     */
    public void extract(TypeElement typeElement) {

        // 跳过被忽略的类
        if (shouldIgnore(typeElement)) {
            return;
        }

        String resourceName = findResourceName(typeElement);
        if (warehouse.containsResource(resourceName)) {
            logWarn("resource '" + resourceName + " already exists");
            return;
        }

        String basePath = findBasePath(typeElement);

        Resource resource = new Resource(resourceName);
        resource.setPath(basePath);

        List<? extends Element> members = elementUtils.getAllMembers(typeElement);
        for (Element e : members) {
            if (e.getKind() == ElementKind.METHOD) {
                Action action = findAction(resource, (ExecutableElement) e);
                if (action != null) {
                    String actionName = action.getName();
                    if (resource.containsAction(actionName)) {
                        logWarn(String.format("action '%s' already exists in resource '%s'", resourceName, actionName));
                    } else {
                        resource.addAction(action);
                    }
                }
            }
        }

        // 如果是一个空的 Controller，那就再见了
        if (!resource.getActions().isEmpty()) {
            warehouse.addResource(resource);
        }
    }

    /**
     * 是否忽略掉类
     */
    private boolean shouldIgnore(TypeElement typeElement) {
        DocIgnore docIgnore = typeElement.getAnnotation(DocIgnore.class);
        return docIgnore != null && docIgnore.value();
    }

    private String findBasePath(TypeElement typeElement) {
        StringBuilder requestMappingBuilder = StringBuilderHolder.resetAndGet();
        getControllerPath(typeElement, requestMappingBuilder);
        return requestMappingBuilder.toString();
    }

    /**
     * 获取 Controller 类上的 RequestMapping 中配置的路径
     * 由于父类上的 RequestMapping 会继承给子类，所以需要递归，所以这里的参数是一个 StringBuilder
     */
    private void getControllerPath(TypeElement element, StringBuilder builder) {
        TypeMirror superclass = element.getSuperclass();
        if (superclass.getKind() != TypeKind.NONE) {
            getControllerPath((TypeElement) typeUtils.asElement(superclass), builder);
        }

        RequestMapping requestMapping = element.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            builder.append(findRequestMapping(requestMapping.value(), requestMapping.path()));
        }
    }

    private String findResourceName(TypeElement typeElement) {
        String simpleNameString = typeElement.getSimpleName().toString();

        if (simpleNameString.endsWith(CONTROLLER)) {
            return simpleNameString.substring(0, simpleNameString.length() - CONTROLLER_LENGTH);
        }

        return simpleNameString;
    }

    /**
     * 这里要考虑支持一下喜闻乐见的 {@link RequestMapping}
     * 但是如果不写 method 字段就比较烦了
     */
    private Action findAction(Resource resource, ExecutableElement element) {

        DocIgnore docIgnore = element.getAnnotation(DocIgnore.class);
        if (docIgnore != null && docIgnore.value()) {
            return null;
        }

        GetMapping getMapping = element.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            String path = findRequestMapping(getMapping.value(), getMapping.path());
            return createAction(resource, element, path, GET,
                    getMapping.consumes(), getMapping.produces());
        }

        PostMapping postMapping = element.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            String path = findRequestMapping(postMapping.value(), postMapping.params());
            return createAction(resource, element, path, POST,
                    postMapping.consumes(), postMapping.produces());
        }

        DeleteMapping deleteMapping = element.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            String path = findRequestMapping(deleteMapping.value(), deleteMapping.params());
            return createAction(resource, element, path, DELETE,
                    deleteMapping.consumes(), deleteMapping.produces());
        }

        PutMapping putMapping = element.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            String path = findRequestMapping(putMapping.value(), putMapping.params());
            return createAction(resource, element, path, PUT,
                    putMapping.consumes(), putMapping.produces());
        }

        PatchMapping patchMapping = element.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            String path = findRequestMapping(patchMapping.value(), patchMapping.params());
            return createAction(resource, element, path, PATCH,
                    patchMapping.consumes(), patchMapping.produces());
        }

        RequestMapping requestMapping = element.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            String path = findRequestMapping(requestMapping.value(), requestMapping.params());
            HttpMethod method = findMethodFromRequestMapping(requestMapping);
            return createAction(resource, element, path, method,
                    requestMapping.consumes(), requestMapping.produces());
        }

        return null;
    }

    private HttpMethod findMethodFromRequestMapping(RequestMapping requestMapping) {
        RequestMethod[] requestMethods = requestMapping.method();
        if (requestMethods.length == 0) {
            // 没指定的直接当 POST 处理
            return POST;
        }

        RequestMethod firstRequestMethod = requestMethods[0];
        switch (firstRequestMethod) {
            case GET:
                return GET;
            case HEAD:
                return HEAD;
            case PUT:
                return PUT;
            case PATCH:
                return PATCH;
            case DELETE:
                return DELETE;
            case OPTIONS:
                return OPTIONS;
            case TRACE:
                return TRACE;
            case POST:
            default:
                return POST;
        }
    }

    private Action createAction(Resource resource, ExecutableElement element, String path, HttpMethod method,
                                String[] consume, String[] produce) {

        String methodName = element.getSimpleName().toString();
        String actionName = NameConversions.methodNameToActionName(methodName);
        String docCommentString = elementUtils.getDocComment(element);
        DocComment docComment = DocComment.create(docCommentString);

        Action action = new Action(resource, actionName, path, method, docComment);

        List<? extends VariableElement> parameters = element.getParameters();
        for (VariableElement parameterElement : parameters) {
            handleParameter(parameterElement, action);
        }

        TypeMirror returnType = element.getReturnType();
        List<String> returnComment = docComment.getReturn();
        Declaration declaration = declarationExtractor.extract(returnType);
        if (declaration.getType() == DeclarationType.OBJECT || declaration.getType() == DeclarationType.ARRAY) {
            Property responseBody = new Property(returnComment, declaration);
            action.setResponseBody(responseBody);
        }

        confirmContentType(action, consume, produce);

        if (action.getRequestBody() != null) {
            NestedDeclaration requestBodyDeclaration = findNestedDeclaration(action.getRequestBody().getDeclaration());
            if (requestBodyDeclaration != null) {
                requestBodyDeclaration.usedBy(action);
            }
        }

        if (action.getResponseBody() != null) {
            NestedDeclaration responseBodyDeclaration = findNestedDeclaration(action.getResponseBody().getDeclaration());
            if (responseBodyDeclaration != null) {
                responseBodyDeclaration.usedBy(action);
            }
        }

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

    private void confirmContentType(Action action, String[] consume, String[] produce) {
        inferRequestContentType(action, consume);
        inferResponseContentType(action, produce);
    }

    private void inferRequestContentType(Action action, String[] consume) {
        if (containsFileParam(action)) {
            action.setRequestContentType(MULTIPART_FORM_DATA);
            return;
        }

        if (action.hasRequestBody()) {
            action.setRequestContentType(SupportedContentType.infer(consume));
        }
    }

    private void inferResponseContentType(Action action, String[] produce) {
        if (action.hasResponseBody()) {
            action.setResponseBodyContentType(SupportedContentType.infer(produce));
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

    private NestedDeclaration findNestedDeclaration(Declaration declaration) {
        if (declaration.getType() == DeclarationType.OBJECT) {
            return ((NestedDeclaration) declaration);
        }

        if (declaration.getType() == DeclarationType.ARRAY) {
            ArrayDeclaration arrayDeclaration = (ArrayDeclaration) declaration;
            Declaration finalItemType = arrayDeclaration.getFinalItemType();
            if (finalItemType.getType() == DeclarationType.OBJECT) {
                return ((NestedDeclaration) finalItemType);
            }
        }

        return null;
    }
}
