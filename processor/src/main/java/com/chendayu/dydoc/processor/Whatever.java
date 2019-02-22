package com.chendayu.dydoc.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

public class Whatever {

    private static final String JAVA_PREFIX = "java.";

    private static final String GETTER_PREFIX = "get";

    private static final String BOOLEAN_GETTER_PREFIX = "is";

    protected final Elements elementUtils;

    protected final Types typesUtils;

    protected final Messager messager;

    protected final Warehouse warehouse;

    public Whatever(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        this.elementUtils = processingEnvironment.getElementUtils();
        this.typesUtils = processingEnvironment.getTypeUtils();
        this.messager = processingEnvironment.getMessager();
        this.warehouse = warehouse;
    }

    public Declaration readAndSaveDeclaration(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        Declaration existsObject = warehouse.getDeclaration(qualifiedName);
        if (existsObject != null) {
            return existsObject;
        }

        return null;
    }

    public ObjectDeclaration findSuperclass(TypeElement typeElement) {
        TypeMirror superclassType = typeElement.getSuperclass();
        if (superclassType.getKind() == TypeKind.DECLARED) {
            TypeElement superclassElement = ((TypeElement) typesUtils.asElement(superclassType));
            return (ObjectDeclaration) readAndSaveDeclaration(superclassElement);
        }
        return null;
    }

    public List<Declaration> findInterfaces(List<? extends TypeMirror> interfaces) {
        if (interfaces.isEmpty()) {
            return Collections.emptyList();
        }

        List<Declaration> objects = new ArrayList<>();
        for (TypeMirror i : interfaces) {
            TypeElement element = (TypeElement) typesUtils.asElement(i);
            String qualifiedName = element.getQualifiedName().toString();
            if (qualifiedName.startsWith(JAVA_PREFIX)) {
                continue;
            }

            objects.add(readAndSaveDeclaration(element));
        }

        return objects;
    }

    public Map<String, Parameter> getTypeParameters(List<? extends TypeParameterElement> elements,
                                                    DocComment docComment) {
        if (elements.isEmpty()) {
            return Collections.emptyMap();
        }

        HashMap<String, Parameter> parameterMap = new HashMap<>(elements.size() * 2);
        for (TypeParameterElement element : elements) {
            String name = element.getSimpleName().toString();
            List<String> description = docComment.getParam(name);
//            parameterMap.put(name, Parameter.createTypeParameter(name, description));
        }
        return parameterMap;
    }

    public List<Parameter> gerProperties(List<? extends Element> members, Map<String, Parameter> typeParameters,
                                         DocComment docComment) {

        LinkedHashMap<String, VariableElement> fieldMap = new LinkedHashMap<>();
        LinkedHashMap<String, ExecutableElement> gettersMap = new LinkedHashMap<>();

        for (Element member : members) {
            String name = member.getSimpleName().toString();
            switch (member.getKind()) {
                case FIELD:
                    fieldMap.put(name, (VariableElement) member);
                    break;
                case METHOD:
                    ExecutableElement method = (ExecutableElement) member;
                    String methodName = method.getSimpleName().toString();
                    if (isGetter(method)) {
                        gettersMap.put(methodName, method);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("illegal member type '" + member.getKind() + "' for " + name);
            }
        }

        if (gettersMap.isEmpty()) {
            return Collections.emptyList();
        }

        for (Map.Entry<String, ExecutableElement> entry : gettersMap.entrySet()) {
            String name = entry.getKey();
            String propertyName = getterToPropertyName(name);
            Parameter parameter = new Parameter(propertyName);
            ExecutableElement getter = entry.getValue();
            List<String> returnComment = DocComment.create(elementUtils.getDocComment(getter)).getReturn();
            if (!returnComment.isEmpty()) {
                parameter.setDescription(returnComment);
            } else {
                VariableElement field = fieldMap.get(name);
                if (field != null) {
                    List<String> description = DocComment.create(elementUtils.getDocComment(field)).getDescription();
                    parameter.setDescription(description);
                }
            }
        }

        return Collections.emptyList();
    }

    //TODO
    private List<Declaration> objectChain(String qualifiedName) {
        return Collections.emptyList();
    }

    private boolean isGetter(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        TypeMirror returnType = element.getReturnType();

        if (name.startsWith(GETTER_PREFIX) || returnType.getKind() != TypeKind.VOID) {
            return true;
        }

        return name.startsWith(BOOLEAN_GETTER_PREFIX) || returnType.getKind() == TypeKind.BOOLEAN;
    }

    private String getterToPropertyName(String methodName) {
        if (methodName.startsWith(GETTER_PREFIX)) {
            String propertyName = methodName.substring(2);
            return Utils.lowerCaseFirst(propertyName);
        }

        String propertyName = methodName.substring(1);
        return Utils.lowerCaseFirst(propertyName);
    }
}
