package com.chendayu.dydoc.processor;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.time.Instant;
import java.util.*;

public class DecalarationExtractor {

    private static final String JAVA_PREFIX = "java.";

    private static final String GETTER_PREFIX = "get";

    private static final String BOOLEAN_GETTER_PREFIX = "is";

    private final Warehouse warehouse;

    private final Toolbox toolbox;

    private final Set<ExecutableElement> objectMethods;

    private final TypeMirror charSequenceType;
    private final TypeMirror numberType;

    private final TypeMirror booleanType;

    private final TypeMirror dateType;
    private final TypeMirror instantType;

    private final TypeMirror collectionType;
    private final TypeMirror mapType;
    private final TypeMirror enumType;

    public DecalarationExtractor(Toolbox toolbox, Warehouse warehouse) {
        this.toolbox = toolbox;
        this.warehouse = warehouse;
        this.objectMethods = initObjectMethods();

        this.charSequenceType = toolbox.getTypeElement(CharSequence.class.getName()).asType();

        this.booleanType = toolbox.getTypeElement(Boolean.class.getName()).asType();

        this.numberType = toolbox.getTypeElement(Number.class.getName()).asType();

        this.dateType = toolbox.getTypeElement(Date.class.getName()).asType();
        this.instantType = toolbox.getTypeElement(Instant.class.getName()).asType();
        this.collectionType = toolbox.erasure(toolbox.getTypeElement(Collection.class.getName()).asType());
        this.mapType = toolbox.erasure(toolbox.getTypeElement(Map.class.getName()).asType());

        this.enumType = toolbox.erasure(toolbox.getTypeElement(Enum.class.getName()).asType());

    }

    private Set<ExecutableElement> initObjectMethods() {

        HashSet<ExecutableElement> objectMethodsSet = new HashSet<>(32, 0.5f);

        TypeElement objectElement = toolbox.getTypeElement(Object.class.getName());
        List<? extends Element> objectMembers = toolbox.getAllMembers(objectElement);
        for (Element objectMember : objectMembers) {
            if (objectElement.getKind() == ElementKind.METHOD) {
                objectMethodsSet.add((ExecutableElement) objectMember);
            }
        }
        return Collections.unmodifiableSet(objectMethodsSet);
    }

    public Declaration extractAndSave(TypeElement typeElement) {

        TypeMirror elementType = typeElement.asType();
        TypeMirror erasedType = toolbox.erasure(elementType);
        TypeKind kind = erasedType.getKind();

        if (isVoid(kind)) {
            return Declaration.VOID;
        }

        if (isNumber(erasedType, kind)) {
            return Declaration.NUMBER;
        }

        if (isCharSequence(erasedType)) {
            return Declaration.STRING;
        }

        if (isTimestamp(erasedType)) {
            return Declaration.TIMESTAMP;
        }

        if (isBoolean(erasedType, kind)) {
            return Declaration.BOOLEAN;
        }

        if (isArray(kind)) {
            ObjectDeclaration componentDeclaration = extractAndSaveFromArray((ArrayType) elementType);
            return Declaration.arrayOf(componentDeclaration);
        }

        if (isCollection(erasedType)) {
            ObjectDeclaration componentDeclaration = extractAndSaveFromCollection((DeclaredType) elementType);
            return Declaration.arrayOf(componentDeclaration);
        }

        if (isDynamic(erasedType)) {
            return Declaration.DYNAMIC;
        }

        if (isEnum(erasedType)) {
            extractAndSaveFromEnum((DeclaredType) elementType);
            return Declaration.ENUM;
        }

        return doExtractAndSave(typeElement, (DeclaredType) elementType);
    }

    private boolean isEnum(TypeMirror erasedType) {
        return toolbox.isSubtype(erasedType, enumType);
    }

    private boolean isDynamic(TypeMirror erasedType) {
        return toolbox.isSubtype(erasedType, mapType);
    }

    private boolean isCollection(TypeMirror erasedType) {
        return toolbox.isSubtype(erasedType, collectionType);
    }

    private boolean isArray(TypeKind kind) {
        return kind == TypeKind.ARRAY;
    }

    private boolean isBoolean(TypeMirror erasedType, TypeKind kind) {
        return kind == TypeKind.BOOLEAN || toolbox.isSameType(erasedType, booleanType);
    }

    private boolean isTimestamp(TypeMirror erasedType) {
        return toolbox.isSameType(erasedType, dateType) || toolbox.isSameType(erasedType, instantType);
    }

    private boolean isCharSequence(TypeMirror erasedType) {
        return toolbox.isSubtype(erasedType, charSequenceType);
    }

    private boolean isNumber(TypeMirror typeMirror, TypeKind kind) {
        return kind == TypeKind.INT ||
                kind == TypeKind.SHORT ||
                kind == TypeKind.BYTE ||
                kind == TypeKind.FLOAT ||
                kind == TypeKind.DOUBLE ||
                toolbox.isSubtype(typeMirror, numberType);
    }

    private boolean isVoid(TypeKind kind) {
        return kind == TypeKind.VOID;
    }

    private ObjectDeclaration extractAndSaveFromArray(ArrayType type) {
        //todo
        return null;
    }

    private ObjectDeclaration extractAndSaveFromCollection(DeclaredType type) {
        //todo
        return null;
    }

    private void extractAndSaveFromEnum(DeclaredType type) {
        //todo
    }

    private ObjectDeclaration doExtractAndSave(TypeElement typeElement, DeclaredType type) {
        //todo
        return null;
    }

    private ObjectDeclaration findSuperclass(TypeElement typeElement) {
        TypeMirror superclassType = typeElement.getSuperclass();
        if (superclassType.getKind() == TypeKind.DECLARED) {
            TypeElement superclassElement = ((TypeElement) toolbox.asElement(superclassType));
            String qualifiedName = superclassElement.getQualifiedName().toString();
            if (isJavaPackageClass(qualifiedName)) {
                return null;
            }
            return (ObjectDeclaration) extractAndSave(superclassElement);
        }
        return null;
    }

    public List<Declaration> findInterfaces(List<? extends TypeMirror> interfaces) {
        if (interfaces.isEmpty()) {
            return Collections.emptyList();
        }

        List<Declaration> objects = new ArrayList<>();
        for (TypeMirror i : interfaces) {
            TypeElement element = (TypeElement) toolbox.asElement(i);
            String qualifiedName = element.getQualifiedName().toString();
            if (qualifiedName.startsWith(JAVA_PREFIX)) {
                continue;
            }

            objects.add(extractAndSave(element));
        }

        return objects;
    }

    public Map<String, Property> getTypeParameters(List<? extends TypeParameterElement> toolbox,
                                                   DocComment docComment) {
        if (toolbox.isEmpty()) {
            return Collections.emptyMap();
        }

        HashMap<String, Property> parameterMap = new HashMap<>(toolbox.size() * 2);
        for (TypeParameterElement element : toolbox) {
            String name = element.getSimpleName().toString();
            List<String> description = docComment.getParam(name);
            Property property = new Property(name, description, Declaration.TYPE_PARAMETER);
            parameterMap.put(name, property);
        }
        return parameterMap;
    }

    public List<ObjectProperty> gerProperties(List<? extends Element> members,
                                              Map<String, Parameter> typeParameters,
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
            List<String> returnComment = DocComment.create(toolbox.getDocComment(getter)).getReturn();
            if (!returnComment.isEmpty()) {
                parameter.setDescription(returnComment);
            } else {
                VariableElement field = fieldMap.get(name);
                if (field != null) {
                    List<String> description = DocComment.create(toolbox.getDocComment(field)).getDescription();
                    parameter.setDescription(description);
                }
            }
        }

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

    private boolean isJavaPackageClass(String qualifiedName) {
        return qualifiedName.startsWith(JAVA_PREFIX);
    }
}
