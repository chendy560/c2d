package com.chendayu.dydoc.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.time.Instant;
import java.util.*;

public class DeclarationExtractor extends InfoExtractor {

    private static final String JAVA_PREFIX = "java.";

    private static final String GETTER_PREFIX = "get";

    private static final String BOOLEAN_GETTER_PREFIX = "is";

    private final Warehouse warehouse;

    private final Set<Element> objectMethods;

    private final TypeMirror voidType;
    private final TypeMirror charSequenceType;
    private final TypeMirror numberType;

    private final TypeMirror booleanType;

    private final TypeMirror dateType;
    private final TypeMirror instantType;

    private final TypeMirror collectionType;
    private final TypeMirror mapType;
    private final TypeMirror enumType;

    public DeclarationExtractor(ProcessingEnvironment environment, Warehouse warehouse) {
        super(environment, warehouse);
        this.warehouse = warehouse;
        this.objectMethods = initObjectMethodNames();

        this.voidType = elementUtils.getTypeElement(Void.class.getName()).asType();
        this.charSequenceType = elementUtils.getTypeElement(CharSequence.class.getName()).asType();

        this.booleanType = elementUtils.getTypeElement(Boolean.class.getName()).asType();

        this.numberType = elementUtils.getTypeElement(Number.class.getName()).asType();

        this.dateType = elementUtils.getTypeElement(Date.class.getName()).asType();
        this.instantType = elementUtils.getTypeElement(Instant.class.getName()).asType();
        this.collectionType = typeUtils.erasure(elementUtils.getTypeElement(Collection.class.getName()).asType());
        this.mapType = typeUtils.erasure(elementUtils.getTypeElement(Map.class.getName()).asType());

        this.enumType = typeUtils.erasure(elementUtils.getTypeElement(Enum.class.getName()).asType());
    }

    private Set<Element> initObjectMethodNames() {

        HashSet<Element> objectMethodsSet = new HashSet<>(32, 0.5f);

        TypeElement objectElement = elementUtils.getTypeElement(Object.class.getName());
        List<? extends Element> objectMembers = elementUtils.getAllMembers(objectElement);
        for (Element objectMember : objectMembers) {
            ElementKind kind = objectMember.getKind();
            if (kind == ElementKind.METHOD) {
                objectMethodsSet.add(objectMember);
            }
        }
        return Collections.unmodifiableSet(objectMethodsSet);
    }

    public Declaration extractAndSave(VariableElement variableElement) {
        TypeMirror typeMirror = variableElement.asType();
        return extractAndSave(typeMirror);
    }

    public Declaration extractAndSave(TypeMirror typeMirror) {
        TypeKind kind = typeMirror.getKind();
        switch (kind) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case SHORT:
            case BYTE:
                return Declaration.NUMBER;
            case BOOLEAN:
                return Declaration.BOOLEAN;
            case CHAR:
                return Declaration.STRING;
            case VOID:
                return Declaration.VOID;
            case TYPEVAR:
                return Declaration.typeArgOf("");
            case DECLARED:
                TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
                return extractAndSave(typeElement);
            default:
                throw new IllegalStateException("unknown type kind: " + kind +
                        " for type mirror: " + typeMirror);
        }
    }

    /**
     * 这里有一个坑，基本类型木有 TypeElement
     */
    public Declaration extractAndSave(TypeElement typeElement) {

        TypeMirror elementType = typeElement.asType();
        TypeMirror erasedType = typeUtils.erasure(elementType);
        TypeKind kind = erasedType.getKind();

        if (isVoid(erasedType)) {
            return Declaration.VOID;
        }

        if (isNumber(erasedType)) {
            return Declaration.NUMBER;
        }

        if (isCharSequence(erasedType)) {
            return Declaration.STRING;
        }

        if (isTimestamp(erasedType)) {
            return Declaration.TIMESTAMP;
        }

        if (isBoolean(erasedType)) {
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

        return doExtractAndSave(typeElement);
    }

    private boolean isEnum(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, enumType);
    }

    private boolean isDynamic(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, mapType);
    }

    private boolean isCollection(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, collectionType);
    }

    private boolean isArray(TypeKind kind) {
        return kind == TypeKind.ARRAY;
    }

    private boolean isBoolean(TypeMirror erasedType) {
        return typeUtils.isSameType(erasedType, booleanType);
    }

    private boolean isTimestamp(TypeMirror erasedType) {
        return typeUtils.isSameType(erasedType, dateType) || typeUtils.isSameType(erasedType, instantType);
    }

    private boolean isCharSequence(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, charSequenceType);
    }

    private boolean isNumber(TypeMirror typeMirror) {
        return typeUtils.isSubtype(typeMirror, numberType);
    }

    private boolean isVoid(TypeMirror typeMirror) {
        return typeUtils.isSameType(typeMirror, voidType);
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

    private ObjectDeclaration doExtractAndSave(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        ObjectDeclaration existsDeclaration = warehouse.getDeclaration(qualifiedName);
        if (existsDeclaration != null) {
            return existsDeclaration;
        }

        ObjectDeclaration result = new ObjectDeclaration(typeElement);
        warehouse.addDeclaration(result);

        DocComment docComment = DocComment.create(elementUtils.getDocComment(typeElement));

        processParents(typeElement, result);
        processTypeParameters(typeElement, docComment, result);
        processFieldAndGetters(typeElement, result);

        return result;
    }

    private void processParents(TypeElement typeElement, ObjectDeclaration result) {

        List<? extends TypeMirror> parentTypes = typeUtils.directSupertypes(typeElement.asType());
        ArrayList<ObjectDeclaration.Parent> parents = new ArrayList<>(parentTypes.size());

        for (TypeMirror parentType : parentTypes) {
            DeclaredType declaredType = (DeclaredType) parentType;
            TypeElement parentElement = getOriginTypeElement(declaredType);

            String qualifiedName = parentElement.getQualifiedName().toString();
            if (isJavaPackageClass(qualifiedName)) {
                break;
            }

            List<Declaration> typeArgs = findTypeArgs(declaredType);
            Declaration declaration = extractAndSave(parentElement);

            Declaration[] ts = typeArgs.toArray(new Declaration[]{});
            ObjectDeclaration.Parent parent = new ObjectDeclaration.Parent(ts, declaration);
            parents.add(parent);
        }

        result.setParents(parents);
    }

    private void processTypeParameters(TypeElement typeElement, DocComment docComment,
                                       ObjectDeclaration result) {
        List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();
        if (typeParameters.isEmpty()) {
            result.setTypeParameters(Collections.emptyList());
            return;
        }

        ArrayList<Property> typeProperties = new ArrayList<>(typeParameters.size());
        for (TypeParameterElement typeParameter : typeParameters) {
            String name = typeParameter.getSimpleName().toString();
            List<String> description = docComment.getParam(name);
            Property property = new Property(name, description, Declaration.typeArgOf(name));
            typeProperties.add(property);
        }

        result.setTypeParameters(typeProperties);
    }

    private List<Declaration> findTypeArgs(DeclaredType declaredType) {
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<Declaration> declarations = new ArrayList<>();
        for (TypeMirror typeArgument : typeArguments) {
            TypeKind kind = typeArgument.getKind();
            Element element = typeUtils.asElement(typeArgument);
            switch (kind) {
                case TYPEVAR:
                    String name = element.getSimpleName().toString();
                    Declaration.TypeArgDeclaration typeArgDeclaration = Declaration.typeArgOf(name);
                    declarations.add(typeArgDeclaration);
                    break;
                case DECLARED:
                    TypeElement typeElement = (TypeElement) element;
                    Declaration declaration = extractAndSave(typeElement);
                    declarations.add(declaration);
                    break;
                default:
                    throw new IllegalArgumentException("unknown type kind for type argument: " + kind.name());
            }
        }
        return declarations;
    }

    public void processFieldAndGetters(TypeElement typeElement, ObjectDeclaration result) {

        List<? extends Element> members = elementUtils.getAllMembers(typeElement);

        LinkedHashMap<String, VariableElement> fieldMap = new LinkedHashMap<>();
        ArrayList<ExecutableElement> getters = new ArrayList<>();

        for (Element member : members) {

            if (objectMethods.contains(member)) {
                continue;
            }
            String name = member.getSimpleName().toString();

            switch (member.getKind()) {
                case FIELD:
                    VariableElement field = (VariableElement) member;
                    if (isInstanceField(field)) {
                        fieldMap.put(name, field);
                    }
                    break;
                case METHOD:
                    ExecutableElement method = (ExecutableElement) member;
                    if (isGetter(method)) {
                        getters.add(method);
                    }
                    break;
                default:
                    break;
            }
        }

        result.setGetters(getters);
        result.setFields(fieldMap);

        if (getters.isEmpty()) {
            result.setProperties(Collections.emptyList());
            return;
        }

        ArrayList<ObjectProperty> properties = new ArrayList<>(getters.size());
        for (ExecutableElement getter : getters) {
            ObjectProperty property = createProperty(getter, fieldMap);
            properties.add(property);
        }
        result.setProperties(properties);
    }

    private ObjectProperty createProperty(ExecutableElement getter, Map<String, VariableElement> fieldMap) {
        String getterName = getter.getSimpleName().toString();
        String name = getterToPropertyName(getterName);
        VariableElement field = fieldMap.get(name);

        List<String> description = findDescription(getter, field);

        TypeMirror returnType = getter.getReturnType();
        Declaration declaration = extractAndSave(returnType);

        return new ObjectProperty(name, description, declaration, field, getter);
    }

    private List<String> findDescription(ExecutableElement getter, VariableElement field) {
        List<String> methodReturnComment = DocComment.create(elementUtils.getDocComment(getter)).getReturn();
        if (!methodReturnComment.isEmpty()) {
            return methodReturnComment;
        }

        if (field != null) {
            String docComment = elementUtils.getDocComment(field);
            DocComment fieldDocComment = DocComment.create(docComment);
            return fieldDocComment.getDescription();
        }

        return Collections.emptyList();
    }

    private boolean isGetter(ExecutableElement element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.STATIC)) {
            return false;
        }

        String name = element.getSimpleName().toString();
        TypeMirror returnType = element.getReturnType();

        if (name.startsWith(GETTER_PREFIX) || returnType.getKind() != TypeKind.VOID) {
            return true;
        }

        return name.startsWith(BOOLEAN_GETTER_PREFIX) || returnType.getKind() == TypeKind.BOOLEAN;
    }

    private String getterToPropertyName(String methodName) {
        if (methodName.startsWith(GETTER_PREFIX)) {
            String propertyName = methodName.substring(3);
            return Utils.lowerCaseFirst(propertyName);
        }

        String propertyName = methodName.substring(2);
        return Utils.lowerCaseFirst(propertyName);
    }

    private boolean isJavaPackageClass(String qualifiedName) {
        return qualifiedName.startsWith(JAVA_PREFIX);
    }

    private boolean isInstanceField(VariableElement field) {
        char firstChar = field.getSimpleName().toString().charAt(0);
        return Character.isLowerCase(firstChar);
    }

    private TypeElement getOriginTypeElement(DeclaredType typeMirror) {
        TypeElement typeElement = (TypeElement) typeMirror.asElement();
        DeclaredType declaredType = (DeclaredType) typeElement.asType();
        return ((TypeElement) declaredType.asElement());
    }
}
