package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({
        "org.springframework.stereotype.Controller",
        "org.springframework.web.bind.annotation.RestController"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleProcessor extends AbstractProcessor {

    private Elements elementUtils;

    private Types typeUtils;

    private Messager messager;

    private Map<String, List<Parameter>> nameObjectMap = new HashMap<>();

    private DocGenerator docGenerator = new DocGenerator();

    private ParameterTypeHelper typeHelper;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        typeHelper = new ParameterTypeHelper(elementUtils, typeUtils);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (final Element element : elements) {
                TypeElement typeElement = (TypeElement) element;
                Resource resource = parseResource(typeElement);
                String doc = docGenerator.generate(resource);
                messager.printMessage(Diagnostic.Kind.NOTE, doc);
            }
        }
        return false;
    }

    private Resource parseResource(TypeElement typeElement) {
        Resource resource = new Resource(findResourceName(typeElement));

        resource.setPath(findRootPath(typeElement));

        List<? extends Element> allMembers = elementUtils.getAllMembers(typeElement);
        for (final Element memberElement : allMembers) {
            Action action = findAction(memberElement);
            if (action != null) {
                resource.addAction(action);
            }
        }

        return resource;
    }

    private String findResourceName(TypeElement typeElement) {
        Name simpleName = typeElement.getSimpleName();
        return simpleName.subSequence(0, simpleName.length() - 10).toString();
    }

    private Action findAction(Element memberElement) {
        if (memberElement.getKind() != ElementKind.METHOD) {
            return null;
        }

        ExecutableElement element = (ExecutableElement) memberElement;

        GetMapping getMapping = element.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return getAction(element, findString(getMapping.value(), getMapping.path()), HttpMethod.GET);
        }

        PostMapping postMapping = element.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return getAction(element, findString(postMapping.value(), postMapping.params()), HttpMethod.POST);
        }

        return null;
    }

    private Action getAction(ExecutableElement element, String path, HttpMethod method) {
        Action action = new Action();
        action.setPath(path);
        action.setName(element.getSimpleName().toString());
        action.setMethod(method);

        String docCommentString = elementUtils.getDocComment(element);
        DocComment docComment = DocComment.create(docCommentString);

        action.setDescription(docComment.getDescription());


        for (VariableElement parameterElement : element.getParameters()) {

            RequestParam requestParam = parameterElement.getAnnotation(RequestParam.class);
            if (requestParam != null) {

                String name = findString(parameterElement.getSimpleName().toString(),
                        requestParam.value(), requestParam.name());
                String description = docComment.getParam(parameterElement.getSimpleName().toString());
                action.addPathVariable(getParameter(name, description, parameterElement));
                continue;
            }

            PathVariable pathVariable = parameterElement.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String name = findString(parameterElement.getSimpleName().toString(),
                        pathVariable.value(), pathVariable.name());
                String description = docComment.getParam(parameterElement.getSimpleName().toString());
                action.addUrlParameter(getParameter(name, description, parameterElement));
            }

            RequestBody requestBody = parameterElement.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                action.setBodyFields(getParameters(parameterElement));
            }
        }
        return action;
    }


    private String findRootPath(TypeElement element) {
        RequestMapping requestMapping = element.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return findString(requestMapping.value(), requestMapping.path());
        }
        return "";
    }

    private String findString(String fallBack, String... ss) {
        for (String s : ss) {
            if (s != null && !s.isEmpty()) {
                return s;
            }
        }
        return fallBack;
    }

    private String findString(String[]... ss) {
        for (String[] s : ss) {
            if (s.length > 0) {
                String s0 = s[0];
                if (s0 != null && !s0.isEmpty()) {
                    return s0;
                }
            }
        }
        return "";
    }

    private Parameter getParameter(String name, String description, VariableElement element) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setDescription(description);

        ParameterType parameterType = typeHelper.findType(element.asType());
        parameter.setType(parameterType);

        return parameter;
    }

    private List<Parameter> getParameters(VariableElement element) {
        TypeMirror typeMirror = element.asType();
        String typeName = typeMirror.toString();
        List<Parameter> parameterList = nameObjectMap.get(typeName);
        if (parameterList != null) {
            return parameterList;
        }

        TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);

        List<VariableElement> variableElements = ElementFilter.fieldsIn(elementUtils.getAllMembers(typeElement));

        List<Parameter> parameters = variableElements.stream()
                .map(e -> getParameter(e.getSimpleName().toString(), commentFirstLine(e), e)).
                        collect(Collectors.toList());

        nameObjectMap.put(typeName, parameterList);
        return parameters;
    }

    private DocComment findDocComment(Element element) {
        return DocComment.create(elementUtils.getDocComment(element));
    }

    private String commentFirstLine(Element element) {
        return findDocComment(element).getDescription();
    }

    private static class ParameterTypeHelper {

        private final Types types;

        private final TypeMirror string;

        private final TypeMirror integerType;
        private final TypeMirror longType;
        private final TypeMirror doubleType;

        private final TypeMirror bigDecimal;
        private final TypeMirror bigInteger;

        private final TypeMirror booleanType;

        private final TypeMirror date;
        private final TypeMirror instant;

        ParameterTypeHelper(Elements elements, Types types) {
            this.types = types;

            this.string = elements.getTypeElement(String.class.getName()).asType();
            this.booleanType = elements.getTypeElement(Boolean.class.getName()).asType();
            this.integerType = elements.getTypeElement(Integer.class.getName()).asType();
            this.longType = elements.getTypeElement(Long.class.getName()).asType();
            this.doubleType = elements.getTypeElement(Double.class.getName()).asType();
            this.bigDecimal = elements.getTypeElement(BigDecimal.class.getName()).asType();
            this.bigInteger = elements.getTypeElement(BigInteger.class.getName()).asType();
            this.date = elements.getTypeElement(Date.class.getName()).asType();
            this.instant = elements.getTypeElement(Instant.class.getName()).asType();
        }

        ParameterType findType(TypeMirror typeMirror) {
            switch (typeMirror.getKind()) {

                case BOOLEAN:
                    return ParameterType.BOOLEAN;

                case CHAR:
                    return ParameterType.STRING;

                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                    return ParameterType.NUMBER;

                case DECLARED:
                    return findType(((DeclaredType) typeMirror));

                default:
                    throw new IllegalArgumentException("unsupport type: " + typeMirror.toString());
            }
        }

        ParameterType findType(DeclaredType type) {
            if (types.isSameType(type, integerType)) {
                return ParameterType.NUMBER;
            }
            if (types.isSameType(type, longType)) {
                return ParameterType.NUMBER;
            }
            if (types.isSameType(type, doubleType)) {
                return ParameterType.NUMBER;
            }
            if (types.isSameType(type, bigDecimal)) {
                return ParameterType.NUMBER;
            }
            if (types.isSameType(type, bigInteger)) {
                return ParameterType.NUMBER;
            }

            if (types.isSameType(type, string)) {
                return ParameterType.STRING;
            }

            if (types.isSameType(type, date)) {
                return ParameterType.TIMESTAMP;
            }
            if (types.isSameType(type, instant)) {
                return ParameterType.TIMESTAMP;
            }

            if (types.isSameType(type, booleanType)) {
                return ParameterType.BOOLEAN;
            }

            return ParameterType.OBJECT;
        }
    }
}
