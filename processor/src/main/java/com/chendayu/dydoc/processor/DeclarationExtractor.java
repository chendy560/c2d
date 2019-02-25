package com.chendayu.dydoc.processor;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.time.Instant;
import java.util.*;

public class DeclarationExtractor {

    private static final String JAVA_PREFIX = "java.";

    private static final String GETTER_PREFIX = "get";

    private static final String BOOLEAN_GETTER_PREFIX = "is";

    private final Warehouse warehouse;

    private final Toolbox toolbox;

    private final Set<Element> objectMethods;

    private final TypeMirror charSequenceType;
    private final TypeMirror numberType;

    private final TypeMirror booleanType;

    private final TypeMirror dateType;
    private final TypeMirror instantType;

    private final TypeMirror collectionType;
    private final TypeMirror mapType;
    private final TypeMirror enumType;

    public DeclarationExtractor(Toolbox toolbox, Warehouse warehouse) {
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

    private Set<Element> initObjectMethods() {

        HashSet<Element> objectMethodsSet = new HashSet<>(32, 0.5f);

        TypeElement objectElement = toolbox.getTypeElement(Object.class.getName());
        List<? extends Element> objectMembers = toolbox.getAllMembers(objectElement);
        for (Element objectMember : objectMembers) {
            if (objectElement.getKind() == ElementKind.METHOD) {
                objectMethodsSet.add(objectMember);
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

        return doExtractAndSave(typeElement);
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

    private ObjectDeclaration doExtractAndSave(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        ObjectDeclaration existsDeclaration = warehouse.getDeclaration(qualifiedName);
        if (existsDeclaration != null) {
            return existsDeclaration;
        }

        ObjectDeclaration result = new ObjectDeclaration(typeElement);
        warehouse.addDeclaration(result);

        DocComment docComment = DocComment.create(toolbox.getDocComment(typeElement));

        processParents(typeElement, result);
        processTypeParameters(typeElement, docComment, result);


        return result;
    }

    private void processParents(TypeElement typeElement, ObjectDeclaration result) {

        List<? extends TypeMirror> parentTypes = toolbox.directSupertypes(typeElement.asType());
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
            Element element = toolbox.asElement(typeArgument);
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

    public List<ObjectProperty> gerProperties(List<? extends Element> members) {

        LinkedHashMap<String, VariableElement> fieldMap = new LinkedHashMap<>();
        ArrayList<ExecutableElement> getters = new ArrayList<>();

        for (Element member : members) {

            if (objectMethods.contains(member)) {
                continue;
            }

            String name = member.getSimpleName().toString();
            switch (member.getKind()) {
                case FIELD:
                    fieldMap.put(name, (VariableElement) member);
                    break;
                case METHOD:
                    ExecutableElement method = (ExecutableElement) member;
                    getters.add(method);
                    break;
                default:
                    break;
            }
        }

        if (getters.isEmpty()) {
            return Collections.emptyList();
        }

        for (ExecutableElement getter : getters) {


        }

        return Collections.emptyList();
    }

    private Property createProperty(ExecutableElement getter, Map<String, VariableElement> fieldMap) {
        String name = getter.getSimpleName().toString();
        List<String> methodReturnDescription = DocComment.create(toolbox.getDocComment(getter)).getReturn();

        //TODO
        return null;

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

    private TypeElement getOriginTypeElement(DeclaredType typeMirror) {
        TypeElement typeElement = (TypeElement) typeMirror.asElement();
        DeclaredType declaredType = (DeclaredType) typeElement.asType();
        return ((TypeElement) declaredType.asElement());
    }
}
