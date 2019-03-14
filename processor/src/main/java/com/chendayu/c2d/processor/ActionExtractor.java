package com.chendayu.c2d.processor;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.chendayu.c2d.processor.Utils.findName;
import static com.chendayu.c2d.processor.Utils.findRequestMapping;

public class ActionExtractor extends InfoExtractor {

    /**
     * 一些会被spring特殊处理的类型，我们就不去处理了
     * RedirectAttributes 和 BindingResult 有接口在里面了，所以没有包含进来
     */
    private static final List<String> ignoreTypeNames = Arrays.asList(
            "org.springframework.web.context.request.RequestAttributes",
            "javax.servlet.ServletRequest",
            "javax.servlet.ServletResponse",
            "javax.servlet.http.HttpSession",
            "java.security.Principal",
            "org.springframework.http.HttpMethod",
            "java.util.Locale",
            "java.util.TimeZone",
            "java.util.ZoneId",
            "java.io.InputStream",
            "java.io.Reader",
            "java.io.OutputStream",
            "java.io.Writer",
            "org.springframework.http.HttpEntity",
            "org.springframework.ui.Model",
            "org.springframework.ui.ModelMap",
            "org.springframework.validation.Errors",
            "org.springframework.web.util.UriBuilder"
    );

    /**
     * 一些并不在请求中，或者我们认为不会去管的字段的注解
     */
    private static final List<Class<? extends Annotation>> ignoreAnnotations = Arrays.asList(
            SessionAttribute.class,
            RequestAttribute.class,
            CookieValue.class,
            RequestHeader.class,
            MatrixVariable.class,
            DocIgnore.class
    );

    private static final EnumSet<DeclarationType> simpleTypes = EnumSet.of(
            DeclarationType.STRING,
            DeclarationType.NUMBER,
            DeclarationType.TIMESTAMP,
            DeclarationType.BOOLEAN,
            DeclarationType.ENUM_CONST,
            DeclarationType.ENUM
    );

    private final DeclarationExtractor declarationExtractor;

    private final List<TypeMirror> ignoreParameterTypes;

    public ActionExtractor(ProcessingEnvironment toolbox, Warehouse warehouse) {
        super(toolbox, warehouse);
        this.declarationExtractor = new DeclarationExtractor(toolbox, warehouse);
        this.ignoreParameterTypes = ignoreParameterTypes();
    }

    private List<TypeMirror> ignoreParameterTypes() {
        ArrayList<TypeMirror> types = new ArrayList<>(ignoreTypeNames.size());
        for (String ignoreTypeName : ignoreTypeNames) {
            TypeElement element = elementUtils.getTypeElement(ignoreTypeName);
            if (element == null) {
                break;
            }
            types.add(typeUtils.erasure(element.asType()));
        }
        return Collections.unmodifiableList(types);
    }

    /**
     * 这里并不支持大家喜闻乐见的 RequestMapping
     * 就是这么任性，谢谢
     */
    public Action findAction(ExecutableElement element) {

        GetMapping getMapping = element.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            String path = findRequestMapping(getMapping.value(), getMapping.path());
            return createAction(element, path, HttpMethod.GET);
        }

        PostMapping postMapping = element.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            String path = findRequestMapping(postMapping.value(), postMapping.params());
            return createAction(element, path, HttpMethod.POST);
        }

        DeleteMapping deleteMapping = element.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            String path = findRequestMapping(deleteMapping.value(), deleteMapping.params());
            return createAction(element, path, HttpMethod.DELETE);
        }

        PutMapping putMapping = element.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            String path = findRequestMapping(putMapping.value(), putMapping.params());
            return createAction(element, path, HttpMethod.PUT);
        }

        PatchMapping patchMapping = element.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            String path = findRequestMapping(patchMapping.value(), patchMapping.params());
            return createAction(element, path, HttpMethod.PATCH);
        }

        return null;
    }

    private Action createAction(ExecutableElement element, String path, HttpMethod method) {

        String methodName = element.getSimpleName().toString();
        String actionName = Utils.upperCaseFirst(methodName);
        String docCommentString = elementUtils.getDocComment(element);
        DocComment docComment = DocComment.create(docCommentString);

        Action action = new Action(actionName, docComment.getDescription());
        action.setPath(path);
        action.setMethod(method);

        List<? extends VariableElement> parameters = element.getParameters();
        for (VariableElement parameterElement : parameters) {
            String parameterName = parameterElement.getSimpleName().toString();
            handleParameter(parameterElement, action, docComment.getParam(parameterName));
        }

        TypeMirror returnType = element.getReturnType();
        List<String> returnComment = docComment.getReturn();
        Declaration declaration = declarationExtractor.extract(returnType);
        Property responseBody = new Property(returnComment, declaration);
        action.setResponseBody(responseBody);

        return action;
    }

    private void handleParameter(VariableElement parameterElement, Action action, List<String> description) {

        if (shouldIgnoreParameter(parameterElement)) {
            return;
        }

        Declaration declaration = declarationExtractor.extract(parameterElement);
        String parameterElementName = parameterElement.getSimpleName().toString();

        RequestParam requestParam = parameterElement.getAnnotation(RequestParam.class);
        if (requestParam != null) {
            String parameterName = findName(parameterElementName,
                    requestParam.value(), requestParam.name());
            if (isSimpleDeclaration(declaration) || isSimpleArray(declaration)) {
                action.addUrlParameter(new Property(parameterName, description, declaration));
            }
            return;
        }

        PathVariable pathVariable = parameterElement.getAnnotation(PathVariable.class);
        if (pathVariable != null) {
            String name = findName(parameterElementName,
                    pathVariable.value(), pathVariable.name());
            if (isSimpleDeclaration(declaration)) {
                action.addPathVariable(new Property(name, description, declaration));
            }
            return;
        }

        RequestBody requestBody = parameterElement.getAnnotation(RequestBody.class);
        if (requestBody != null) {
            Property property = new Property(description, declaration);
            action.setRequestBody(property);
            return;
        }

        if (isSimpleDeclaration(declaration) || isSimpleArray(declaration)) {
            Property property = new Property(parameterElementName, description,
                    declaration);
            action.addUrlParameter(property);
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
        }
    }

    private boolean shouldIgnoreParameter(VariableElement parameter) {
        TypeMirror type = parameter.asType();
        TypeMirror erased = typeUtils.erasure(type);

        for (TypeMirror ignoreParameterType : ignoreParameterTypes) {
            if (typeUtils.isSubtype(erased, ignoreParameterType)) {
                return true;
            }
            if (typeUtils.isSameType(erased, ignoreParameterType)) {
                return true;
            }
        }

        for (Class<? extends Annotation> ignoreAnnotation : ignoreAnnotations) {
            if (parameter.getAnnotation(ignoreAnnotation) != null) {
                return true;
            }
        }

        return false;
    }

    private boolean isSimpleDeclaration(Declaration declaration) {
        DeclarationType type = declaration.getType();
        return simpleTypes.contains(type);
    }

    private boolean isSimpleArray(Declaration declaration) {
        if (declaration.getType() == DeclarationType.ARRAY) {
            Declarations.ArrayDeclaration arrayDeclaration = (Declarations.ArrayDeclaration) declaration;
            Declaration componentType = arrayDeclaration.getComponentType();
            return isSimpleDeclaration(componentType);
        }
        return false;
    }
}
