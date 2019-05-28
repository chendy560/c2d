package com.chendayu.c2d.processor.declaration;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.Utils;
import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.processor.DescriptionProcessor;
import com.chendayu.c2d.processor.processor.DocIgnoreProcessor;
import com.chendayu.c2d.processor.processor.JacksonProcessor;
import com.chendayu.c2d.processor.processor.LombokProcessor;
import com.chendayu.c2d.processor.processor.NestedDeclarationPostProcessor;
import com.chendayu.c2d.processor.property.Property;

import org.springframework.web.multipart.MultipartFile;

import static com.chendayu.c2d.processor.declaration.ArrayDeclaration.arrayOf;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.BOOLEAN;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.DYNAMIC;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.ENUM_CONST;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.FILE;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.NUMBER;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.STRING;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.TIMESTAMP;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.UNKNOWN;
import static com.chendayu.c2d.processor.declaration.SimpleDeclaration.VOID;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * 数据类型提取器
 */
public class DeclarationExtractor extends InfoExtractor {

    /**
     * lombok 的 Data 注解，用于判断是否引入了 lombok
     */
    private static final String LOMBOK_DATA = "lombok.Data";

    /**
     * jackson 的 JsonIgnore 注解，用于判断是否引入了 jackson
     */
    private static final String JACKSON_JSON_IGNORE = "com.fasterxml.jackson.annotation.JsonIgnore";

    /**
     * java包，在提取父类和接口时会被忽略
     */
    private static final String JAVA_PREFIX = "java.";

    /**
     * javax包，在提取父类和接口时会被忽略
     */
    private static final String JAVAX_PREFIX = "javax.";

    /**
     * getter方法的前缀
     */
    private static final String GETTER_PREFIX = "get";

    /**
     * getter方法前缀的长度
     */
    private static final int GETTER_LENGTH = GETTER_PREFIX.length();

    /**
     * is方法的前缀
     */
    private static final String BOOLEAN_GETTER_PREFIX = "is";

    /**
     * is方法的前缀的长度
     */
    private static final int BOOLEAN_GETTER_LENGTH = BOOLEAN_GETTER_PREFIX.length();

    /**
     * setter方法前缀
     */
    private static final String SETTER_PREFIX = "set";

    /**
     * setter方法前缀长度
     */
    private static final int SETTER_LENGTH = SETTER_PREFIX.length();

    /**
     * 当数组内的元素类型无法解析时返回这个类型
     */
    private static final Declaration UNKNOWN_ARRAY = arrayOf(UNKNOWN);

    /**
     * {@link Void} 类型，区别于 void
     */
    private final TypeMirror voidType;

    /**
     * {@link CharSequence} 类型，相当于字符串
     */
    private final TypeMirror charSequenceType;

    /**
     * {@link Number} 类型，各种数字包装类，大整形，大浮点等等的接口
     */
    private final TypeMirror numberType;

    /**
     * {@link Boolean}
     */
    private final TypeMirror booleanType;

    /**
     * {@link Date}
     */
    private final TypeMirror dateType;

    /**
     * {@link Instant}
     */
    private final TypeMirror instantType;

    /**
     * {@link Collection}
     */
    private final TypeMirror collectionType;

    /**
     * {@link Map}
     */
    private final TypeMirror mapType;

    /**
     * {@link Enum} 所有枚举的父类
     */
    private final TypeMirror enumType;

    /**
     * {@link MultipartFile} spring 处理文件上传的类型
     */
    private final TypeMirror multiPartFileType;

    /**
     * 后处理器们
     */
    private final List<NestedDeclarationPostProcessor> postProcessors;

    public DeclarationExtractor(ProcessingEnvironment environment, Warehouse warehouse) {
        super(environment, warehouse);

        this.voidType = getDeclaredType(Void.class);
        this.charSequenceType = getDeclaredType(CharSequence.class);

        this.booleanType = getDeclaredType(Boolean.class);

        this.numberType = getDeclaredType(Number.class);

        this.dateType = getDeclaredType(Date.class);
        this.instantType = getDeclaredType(Instant.class);

        this.collectionType = getDeclaredType(Collection.class, 1);

        this.mapType = getDeclaredType(Map.class, 2);

        this.enumType = getDeclaredType(Enum.class, 1);

        this.multiPartFileType = getDeclaredType(MultipartFile.class);

        this.postProcessors = initPostProcessors(environment);
    }

    private List<NestedDeclarationPostProcessor> initPostProcessors(ProcessingEnvironment environment) {
        List<NestedDeclarationPostProcessor> processors = new ArrayList<>();

        processors.add(new DocIgnoreProcessor(environment));
        processors.add(new DescriptionProcessor(environment));

        TypeElement lombokData = elementUtils.getTypeElement(LOMBOK_DATA);
        if (lombokData != null) {
            LombokProcessor lombokProcessor =
                    new LombokProcessor(environment);
            processors.add(lombokProcessor);
        }

        TypeElement jsonIgnore = elementUtils.getTypeElement(JACKSON_JSON_IGNORE);
        if (jsonIgnore != null) {
            JacksonProcessor jacksonProcessor =
                    new JacksonProcessor(environment);
            processors.add(jacksonProcessor);
        }

        processors.sort(Comparator.comparing(NestedDeclarationPostProcessor::getOrder));
        return Collections.unmodifiableList(processors);
    }

    /**
     * 需要解析成 Declaration 的东西只有方法参数和方法返回值，这里是方法参数
     */
    public Declaration extract(VariableElement variableElement) {
        return extract(variableElement.asType());
    }

    /**
     * 需要解析成 Declaration 的东西只有方法参数和方法返回值，这里方法是返回值
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
                return NUMBER;
            case BOOLEAN:
                return BOOLEAN;
            case CHAR:
                return STRING; // 真的有人会用吗？
            case VOID:
                return VOID; // void 和 Void 不是一个东西，这里的是 void
            case ARRAY:
                return extractFromArrayType((ArrayType) typeMirror);
            case DECLARED:
                DeclaredType declaredType = (DeclaredType) typeMirror;
                return extractFromDeclaredType(declaredType);
            case TYPEVAR:
                TypeVariable typeVariable = (TypeVariable) typeMirror;
                String name = typeVariable.asElement().getSimpleName().toString();
                return new TypeVarDeclaration(name);
            default:
                String message = "unknown type kind: " + kind +
                        " for type mirror: " + typeMirror;
                logWarn(message);
                return UNKNOWN;
        }
    }

    private Declaration extractFromDeclaredType(DeclaredType declaredType) {

        if (typeUtils.isSameType(declaredType, voidType)) {
            return VOID;
        }

        if (typeUtils.isAssignable(declaredType, numberType)) {
            return NUMBER;
        }

        if (typeUtils.isAssignable(declaredType, charSequenceType)) {
            return STRING;
        }

        if (typeUtils.isAssignable(declaredType, dateType) ||
                typeUtils.isAssignable(declaredType, instantType)) {
            return TIMESTAMP;
        }

        if (typeUtils.isSameType(declaredType, booleanType)) {
            return BOOLEAN;
        }

        if (typeUtils.isAssignable(declaredType, collectionType)) {
            return extractFromCollection(declaredType);
        }

        if (typeUtils.isAssignable(declaredType, mapType)) {
            return DYNAMIC;
        }

        if (typeUtils.isSameType(declaredType, multiPartFileType)) {
            return FILE;
        }

        if (typeUtils.isAssignable(declaredType, enumType)) {
            return extractFromEnum(declaredType);
        }

        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String qualifiedName = typeElement.getQualifiedName().toString();
        if (isJavaPackageClass(qualifiedName)) {
            return UNKNOWN;
        }

        NestedDeclaration nestedDeclaration = extractNestedDeclarationFromTypeElement(typeElement);

        List<Declaration> typeArgs = findTypeArgs(declaredType);
        if (!typeArgs.isEmpty()) {
            return nestedDeclaration.withTypeArguments(typeArgs);
        }

        return nestedDeclaration;
    }

    private EnumDeclaration extractFromEnum(DeclaredType declaredType) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String qualifiedName = typeElement.getQualifiedName().toString();
        EnumDeclaration existEnum = warehouse.getEnumDeclaration(qualifiedName);
        if (existEnum != null) {
            return existEnum;
        }

        List<? extends Element> members = elementUtils.getAllMembers(typeElement);
        ArrayList<Property> properties = new ArrayList<>();
        for (Element member : members) {
            Property property = asEnumConstant(member);
            if (property != null) {
                properties.add(property);
            }
        }

        String name = typeElement.getSimpleName().toString();
        List<String> description = DocComment.create(elementUtils.getDocComment(typeElement))
                .getDescription();
        EnumDeclaration enumDeclaration = new EnumDeclaration(name, qualifiedName, properties, description);
        warehouse.addEnumDeclaration(enumDeclaration);

        return enumDeclaration;
    }

    private Property asEnumConstant(Element element) {
        ElementKind elementKind = element.getKind();
        if (elementKind != ElementKind.ENUM_CONSTANT) {
            return null;
        }

        VariableElement variableElement = (VariableElement) element;
        String name = variableElement.getSimpleName().toString();
        List<String> description = DocComment.create(elementUtils.getDocComment(variableElement))
                .getDescription();
        return new Property(name, description, ENUM_CONST);
    }

    private Declaration extractFromArrayType(ArrayType type) {
        TypeMirror componentType = type.getComponentType();
        if (componentType.getKind() == TypeKind.CHAR) {
            return STRING;
        }
        Declaration declaration = extract(componentType);
        return arrayOf(declaration);
    }

    private Declaration extractFromCollection(DeclaredType type) {
        List<? extends TypeMirror> typeArguments = type.getTypeArguments();

        if (typeArguments.isEmpty()) {
            return UNKNOWN_ARRAY;
        }

        TypeMirror typeMirror = typeArguments.get(0);
        Declaration declaration = extract(typeMirror);
        return arrayOf(declaration);
    }


    private NestedDeclaration extractNestedDeclarationFromTypeElement(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        NestedDeclaration existsDeclaration = warehouse.getDeclaration(qualifiedName);
        if (existsDeclaration != null) {
            return existsDeclaration;
        }

        DocComment docComment = DocComment.create(elementUtils.getDocComment(typeElement));
        NestedDeclaration result = new NestedDeclaration(typeElement);

        // 直接塞进仓库，避免无限递归
        warehouse.addDeclaration(result);

        // 首先处理父类
        processParents(typeElement, result);

        // 处理自己的字段，父类和接口的 field 和 getter 直接可以在子类中拿到，
        Map<String, Property> propertyMap = getPropertyMap(typeElement);

        result.applyProperties(propertyMap.values());

        // 最后设置好类型参数
        processTypeParameters(typeElement, docComment, result);

        // 后处理器们
        postProcess(result);

        return result;
    }

    private void postProcess(NestedDeclaration result) {
        for (NestedDeclarationPostProcessor postProcessor : postProcessors) {
            postProcessor.process(result);
        }
    }

    private void processParents(TypeElement typeElement, NestedDeclaration result) {

        List<? extends TypeMirror> parentTypes = typeUtils.directSupertypes(typeElement.asType());

        for (TypeMirror parentType : parentTypes) {
            DeclaredType declaredType = (DeclaredType) parentType;
            TypeElement parentElement = (TypeElement) declaredType.asElement();

            String qualifiedName = parentElement.getQualifiedName().toString();

            // 默认情况下，java和javax的东西都不处理，其实这里更多是在优化
            if (isJavaPackageClass(qualifiedName)) {
                continue;
            }

            // 能走到这一步的都是对象了
            Declaration parentDeclaration = extractFromDeclaredType(declaredType);
            if (parentDeclaration.getType() == DeclarationType.OBJECT) {
                NestedDeclaration declaration = (NestedDeclaration) parentDeclaration;
                result.applyParent(declaration);
            }
        }
    }

    private void processTypeParameters(TypeElement typeElement, DocComment docComment,
                                       NestedDeclaration result) {
        List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();
        if (typeParameters.isEmpty()) {
            result.setTypeParameters(Collections.emptyList());
            return;
        }

        ArrayList<TypeVarDeclaration> declarations = new ArrayList<>(typeParameters.size());
        for (TypeParameterElement typeParameter : typeParameters) {
            String name = typeParameter.getSimpleName().toString();
            List<String> description = docComment.getTypeParam(name);
            TypeVarDeclaration typeVarDeclaration = new TypeVarDeclaration(name, description);
            declarations.add(typeVarDeclaration);
        }

        result.setTypeParameters(declarations);
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

    private Map<String, Property> getPropertyMap(TypeElement typeElement) {

        LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

        List<? extends Element> members = typeElement.getEnclosedElements();
        for (Element member : members) {

            switch (member.getKind()) {
                case FIELD:
                    handleField(propertyMap, (VariableElement) member);
                    break;
                case METHOD:
                    ExecutableElement method = (ExecutableElement) member;
                    if (isGetter(method)) {
                        handleGetter(propertyMap, member, method);
                    } else if (isSetter(method)) {
                        handleSetter(propertyMap, member, method);
                    }
                    break;
                default:
                    break;
            }
        }

        return propertyMap;
    }

    private void handleSetter(LinkedHashMap<String, Property> propertyMap, Element member, ExecutableElement method) {
        String methodName = member.getSimpleName().toString();
        String propertyName = setterToPropertyName(methodName);
        Property property = propertyMap.get(propertyName);
        if (property == null) {
            TypeMirror returnType = method.getReturnType();
            Declaration declaration = extract(returnType);
            Property newProperty = new Property(propertyName, declaration);
            newProperty.setSetter(method);
            newProperty.setSettable(true);
            propertyMap.put(propertyName, newProperty);
        } else {
            property.setSetter(method);
            property.setSettable(true);
        }
    }

    private void handleGetter(LinkedHashMap<String, Property> propertyMap, Element member, ExecutableElement method) {
        String methodName = member.getSimpleName().toString();
        String propertyName = getterToPropertyName(methodName);
        Property property = propertyMap.get(propertyName);
        if (property == null) {
            TypeMirror returnType = method.getReturnType();
            Declaration declaration = extract(returnType);
            Property newProperty = new Property(propertyName, declaration);
            newProperty.setGetter(method);
            newProperty.setGettable(true);
            propertyMap.put(propertyName, newProperty);
        } else {
            property.setGetter(method);
            property.setGettable(true);
        }
    }

    private void handleField(LinkedHashMap<String, Property> propertyMap, VariableElement member) {
        if (isInstanceField(member)) {
            String fieldName = member.getSimpleName().toString();
            Property property = propertyMap.get(fieldName);
            if (property == null) {
                Declaration declaration = this.extract(member);
                Property newProperty = new Property(fieldName, declaration);
                newProperty.setField(member);
                newProperty.setSettable(false);
                newProperty.setGettable(false);
                propertyMap.put(fieldName, newProperty);
            } else {
                property.setField(member);
            }
        }
    }

    private boolean isGetter(ExecutableElement element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(STATIC)) {
            return false;
        }

        if (!element.getParameters().isEmpty()) {
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

    private boolean isSetter(ExecutableElement element) {
        Set<Modifier> modifiers = element.getModifiers();

        if (modifiers.contains(STATIC)) {
            return false;
        }

        if (element.getParameters().size() != 1) {
            return false;
        }

        String name = element.getSimpleName().toString();

        return name.startsWith(SETTER_PREFIX) && name.length() > SETTER_LENGTH;
    }

    private String getterToPropertyName(String methodName) {
        if (methodName.startsWith(GETTER_PREFIX)) {
            String propertyName = methodName.substring(GETTER_LENGTH);
            return Utils.lowerCaseFirst(propertyName);
        }

        String propertyName = methodName.substring(BOOLEAN_GETTER_LENGTH);
        return Utils.lowerCaseFirst(propertyName);
    }

    private String setterToPropertyName(String methodName) {
        String propertyName = methodName.substring(SETTER_LENGTH);
        return Utils.lowerCaseFirst(propertyName);
    }

    private boolean isJavaPackageClass(String qualifiedName) {
        return qualifiedName.startsWith(JAVA_PREFIX)
                || qualifiedName.startsWith(JAVAX_PREFIX);
    }

    private boolean isInstanceField(VariableElement field) {
        if (field.getModifiers().contains(STATIC)) {
            return false;
        }
        char firstChar = field.getSimpleName().toString().charAt(0);
        return Character.isLowerCase(firstChar);
    }

    private TypeMirror getDeclaredType(Class<?> typeClass) {
        return getDeclaredType(typeClass, 0);
    }
}
