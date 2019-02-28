package com.chendayu.c2d.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.time.Instant;
import java.util.*;

import static com.chendayu.c2d.processor.Declarations.UNKNOWN;
import static com.chendayu.c2d.processor.Declarations.arrayOf;

public class DeclarationExtractor extends InfoExtractor {

    private static final String LOMBOK_DATA = "lombok.Data";

    private static final String JAVA_PREFIX = "java.";
    private static final String JAVAX_PREFIX = "javax.";

    private static final String GETTER_PREFIX = "get";
    private static final int GETTER_LENGTH = GETTER_PREFIX.length();

    private static final String BOOLEAN_GETTER_PREFIX = "is";
    private static final int BOOLEAN_GETTER_LENGTH = BOOLEAN_GETTER_PREFIX.length();

    private static final Declaration UNKNOWN_ARRAY = arrayOf(UNKNOWN);

    private final Set<Element> objectMethods;

    private final TypeMirror voidType;

    private final TypeMirror charSequenceType;

    private final TypeMirror numberType;

    private final TypeMirror booleanType;

    private final TypeMirror dateType;
    private final TypeMirror instantType;

    private final TypeMirror iterableType;
    private final TypeMirror collectionType;
    private final TypeMirror listType;
    private final TypeMirror setType;

    private final TypeMirror mapType;
    private final TypeMirror enumType;

    private final SortedSet<ObjectDeclarationPostProcessor> postProcessors;

    public DeclarationExtractor(ProcessingEnvironment environment, Warehouse warehouse) {
        super(environment, warehouse);
        this.objectMethods = initObjectMethods();

        this.voidType = elementUtils.getTypeElement(Void.class.getName()).asType();
        this.charSequenceType = elementUtils.getTypeElement(CharSequence.class.getName()).asType();

        this.booleanType = elementUtils.getTypeElement(Boolean.class.getName()).asType();

        this.numberType = elementUtils.getTypeElement(Number.class.getName()).asType();

        this.dateType = elementUtils.getTypeElement(Date.class.getName()).asType();
        this.instantType = elementUtils.getTypeElement(Instant.class.getName()).asType();
        this.iterableType = typeUtils.erasure(elementUtils.getTypeElement(Iterable.class.getName()).asType());
        this.collectionType = typeUtils.erasure(elementUtils.getTypeElement(Collection.class.getName()).asType());
        this.listType = typeUtils.erasure(elementUtils.getTypeElement(List.class.getName()).asType());
        this.setType = typeUtils.erasure(elementUtils.getTypeElement(Set.class.getName()).asType());

        this.mapType = typeUtils.erasure(elementUtils.getTypeElement(Map.class.getName()).asType());

        this.enumType = typeUtils.erasure(elementUtils.getTypeElement(Enum.class.getName()).asType());

        this.postProcessors = initPostProcessors(environment);
    }

    private Set<Element> initObjectMethods() {

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

    private SortedSet<ObjectDeclarationPostProcessor> initPostProcessors(ProcessingEnvironment environment) {
        TreeSet<ObjectDeclarationPostProcessor> processors =
                new TreeSet<>(Comparator.comparing(ObjectDeclarationPostProcessor::getOrder));

        TypeElement lombokData = elementUtils.getTypeElement(LOMBOK_DATA);
        if (lombokData != null) {
            LombokObjectDeclarationPostProcessor lombokProcessor =
                    new LombokObjectDeclarationPostProcessor(environment, this);
            processors.add(lombokProcessor);
        }

        return Collections.unmodifiableSortedSet(processors);
    }

    /**
     * 两个真正的入口之一，需要解析成 Declaration 的东西只有方法参数和方法返回值，这里是方法参数
     */
    public Declaration extract(VariableElement variableElement) {
        return extract(variableElement.asType());
    }

    /**
     * 两个真正的入口之一，需要解析成 Declaration 的东西只有方法参数和方法返回值，这里是返回值
     */
    public Declaration extract(TypeMirror typeMirror) {
        TypeKind kind = typeMirror.getKind();
        switch (kind) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case SHORT: // 真的有人会用吗？
            case BYTE: // 真的有人会用吗？
                return Declarations.NUMBER;
            case BOOLEAN:
                return Declarations.BOOLEAN;
            case CHAR:
                return Declarations.STRING; // 真的有人会用吗？
            case VOID:
                return Declarations.VOID; // void 和 Void 不是一个东西，这里的是 void
            case ARRAY:
                return extractFromArrayType((ArrayType) typeMirror);
            case DECLARED:
                DeclaredType declaredType = (DeclaredType) typeMirror;
                return extractFromDeclaredType(declaredType);
            case TYPEVAR:
                TypeVariable typeVariable = (TypeVariable) typeMirror;
                String name = typeVariable.asElement().getSimpleName().toString();
                return Declarations.typeArgOf(name);
            default:
                throw new IllegalStateException("unknown type kind: " + kind +
                        " for type mirror: " + typeMirror);
        }
    }

    /**
     * 解析对象的入口，只有这里才能解析出指定的泛型
     */
    private Declaration extractFromDeclaredType(DeclaredType declaredType) {

        TypeMirror erasedType = typeUtils.erasure(declaredType);

        if (isVoid(erasedType)) {
            return Declarations.VOID;
        }

        if (isNumber(erasedType)) {
            return Declarations.NUMBER;
        }

        if (isCharSequence(erasedType)) {
            return Declarations.STRING;
        }

        if (isTimestamp(erasedType)) {
            return Declarations.TIMESTAMP;
        }

        if (isBoolean(erasedType)) {
            return Declarations.BOOLEAN;
        }

        if (isIterable(erasedType)) {
            return extractFromIterable(declaredType);
        }

        if (isDynamic(erasedType)) {
            return Declarations.DYNAMIC;
        }

        if (isEnum(erasedType)) {
            // todo
            return Declarations.ENUM;
        }

        TypeElement typeElement = (TypeElement) declaredType.asElement();
        ObjectDeclaration objectDeclaration = extractObjectDeclarationFromTypeElement(typeElement);

        List<Declaration> typeArgs = findTypeArgs(declaredType);
        if (!typeArgs.isEmpty()) {
            return objectDeclaration.withTypeArgs(typeArgs);
        }

        return objectDeclaration;
    }

    private boolean isEnum(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, enumType);
    }

    private boolean isDynamic(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, mapType);
    }

    private boolean isIterable(TypeMirror erasedType) {
        return typeUtils.isSubtype(erasedType, iterableType);
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

    private Declaration extractFromArrayType(ArrayType type) {
        TypeMirror componentType = type.getComponentType();
        if (componentType.getKind() == TypeKind.CHAR) {
            return Declarations.STRING;
        }
        Declaration declaration = extract(componentType);
        return arrayOf(declaration);
    }

    private Declaration extractFromIterable(DeclaredType type) {

        TypeMirror erased = typeUtils.erasure(type);
        if (isSimpleIterable(erased)) {
            List<? extends TypeMirror> typeArguments = type.getTypeArguments();
            if (!typeArguments.isEmpty()) {
                TypeMirror typeMirror = typeArguments.get(0);
                Declaration declaration = extract(typeMirror);
                return arrayOf(declaration);
            }
        }

        return UNKNOWN_ARRAY;
    }

    private boolean isSimpleIterable(TypeMirror erasedType) {
        return typeUtils.isSameType(erasedType, listType)
                || typeUtils.isSameType(erasedType, setType)
                || typeUtils.isSameType(erasedType, collectionType)
                || typeUtils.isSameType(erasedType, iterableType);
    }

    private ObjectDeclaration extractObjectDeclarationFromTypeElement(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        ObjectDeclaration existsDeclaration = warehouse.getDeclaration(qualifiedName);
        if (existsDeclaration != null) {
            return existsDeclaration;
        }

        ObjectDeclaration result = new ObjectDeclaration(typeElement);
        // 直接塞进仓库，避免无限递归
        warehouse.addDeclaration(result);

        DocComment docComment = DocComment.create(elementUtils.getDocComment(typeElement));
        result.setDescription(docComment.getDescription());

        // 首先处理自己的字段，父类和接口的 field 和 getter 直接可以在子类中拿到，
        initFieldsAndProperties(typeElement, result);

        // 获取父类和接口更多是为了在特殊情况下补齐文档
        processParents(typeElement, result);

        // 最后设置好类型参数
        processTypeParameters(typeElement, docComment, result);

        // 后处理器们
        postProcess(result);

        return result;
    }

    private void postProcess(ObjectDeclaration result) {
        for (ObjectDeclarationPostProcessor postProcessor : postProcessors) {
            postProcessor.process(result);
        }
    }

    private void processParents(TypeElement typeElement, ObjectDeclaration result) {

        List<? extends TypeMirror> parentTypes = typeUtils.directSupertypes(typeElement.asType());

        for (TypeMirror parentType : parentTypes) {
            DeclaredType declaredType = (DeclaredType) parentType;
            TypeElement parentElement = getOriginTypeElement(declaredType);

            String qualifiedName = parentElement.getQualifiedName().toString();

            // 默认情况下，java和javax的东西都不处理，其实这里更多是在优化
            if (isJavaPackageClass(qualifiedName)) {
                break;
            }

            // 能走到这一步的都是对象了
            ObjectDeclaration declaration = (ObjectDeclaration) extractFromDeclaredType(declaredType);

            List<Property> properties = declaration.getProperties();
            for (Property property : properties) {

                String propertyName = property.getName();

                if (result.containsProperty(propertyName)) {
                    result.addPropertyDescriptionIfNotExists(property);
                    continue;
                }

                Declaration propertyDeclaration = property.getDeclaration();
                List<String> propertyDescription = property.getDescription();
                ObjectProperty newProperty = new ObjectProperty(
                        propertyName, propertyDescription, propertyDeclaration, null, null);
                result.addProperty(newProperty);
            }
        }
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
            List<String> description = docComment.getTypeParam(name);
            Property property = new Property(name, description, Declarations.typeArgOf(name));
            typeProperties.add(property);
        }

        result.setTypeParameters(typeProperties);
    }

    private List<Declaration> findTypeArgs(DeclaredType declaredType) {
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<Declaration> typeArgs = new ArrayList<>();
        for (TypeMirror typeArgument : typeArguments) {
            Declaration declaration = extract(typeArgument);
            typeArgs.add(declaration);
        }
        return typeArgs;
    }

    private void initFieldsAndProperties(TypeElement typeElement, ObjectDeclaration result) {

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

        result.setFieldMap(fieldMap);

        for (ExecutableElement getter : getters) {
            ObjectProperty property = createProperty(getter, fieldMap);
            result.addProperty(property);
        }
    }

    private ObjectProperty createProperty(ExecutableElement getter, Map<String, VariableElement> fieldMap) {
        String getterName = getter.getSimpleName().toString();
        String name = getterToPropertyName(getterName);
        VariableElement field = fieldMap.get(name);

        List<String> description = findDescription(getter, field);

        TypeMirror returnType = getter.getReturnType();
        Declaration declaration = extract(returnType);

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
        TypeKind kind = element.getReturnType().getKind();

        if (kind == TypeKind.VOID) {
            return false;
        }

        if (name.startsWith(GETTER_PREFIX) && name.length() > GETTER_LENGTH) {
            return true;
        }

        return name.startsWith(BOOLEAN_GETTER_PREFIX)
                && name.length() > BOOLEAN_GETTER_LENGTH
                && kind == TypeKind.BOOLEAN;
    }

    private String getterToPropertyName(String methodName) {
        if (methodName.startsWith(GETTER_PREFIX)) {
            String propertyName = methodName.substring(GETTER_LENGTH);
            return Utils.lowerCaseFirst(propertyName);
        }

        String propertyName = methodName.substring(BOOLEAN_GETTER_LENGTH);
        return Utils.lowerCaseFirst(propertyName);
    }

    private boolean isJavaPackageClass(String qualifiedName) {
        return qualifiedName.startsWith(JAVA_PREFIX)
                || qualifiedName.startsWith(JAVAX_PREFIX);
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
