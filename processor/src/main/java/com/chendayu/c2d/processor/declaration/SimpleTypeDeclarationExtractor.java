package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.AbstractComponent;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static com.chendayu.c2d.processor.declaration.Declarations.BOOLEAN;
import static com.chendayu.c2d.processor.declaration.Declarations.DYNAMIC;
import static com.chendayu.c2d.processor.declaration.Declarations.FILE;
import static com.chendayu.c2d.processor.declaration.Declarations.NUMBER;
import static com.chendayu.c2d.processor.declaration.Declarations.STRING;
import static com.chendayu.c2d.processor.declaration.Declarations.TIMESTAMP;
import static com.chendayu.c2d.processor.declaration.Declarations.VOID;
import static com.chendayu.c2d.processor.declaration.Declarations.typeArgOf;

/**
 * 简单类型抽取器
 * 简单类型其实就是不会内嵌的类型
 */
public class SimpleTypeDeclarationExtractor extends AbstractComponent implements IDeclarationExtractor {

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
     * {@link Map}
     */
    private final TypeMirror mapType;

    /**
     * {@link MultipartFile}
     */
    private final TypeMirror multiPartFileType;

    public SimpleTypeDeclarationExtractor(ProcessingEnvironment processingEnvironment) {
        // 简单类型的 declaration 不需要保存起来
        super(processingEnvironment);

        this.voidType = elementUtils.getTypeElement(Void.class.getName()).asType();
        this.charSequenceType = elementUtils.getTypeElement(CharSequence.class.getName()).asType();

        this.booleanType = elementUtils.getTypeElement(Boolean.class.getName()).asType();

        this.numberType = elementUtils.getTypeElement(Number.class.getName()).asType();

        this.dateType = elementUtils.getTypeElement(Date.class.getName()).asType();
        this.instantType = elementUtils.getTypeElement(Instant.class.getName()).asType();

        this.mapType = typeUtils.erasure(elementUtils.getTypeElement(Map.class.getName()).asType());

        this.multiPartFileType = elementUtils.getTypeElement(MultipartFile.class.getName()).asType();
    }

    @Override
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
            case TYPEVAR:
                TypeVariable typeVariable = (TypeVariable) typeMirror;
                String name = typeVariable.asElement().getSimpleName().toString();
                return typeArgOf(name);
            case ARRAY:
                return null;
            default:
                break;
        }

        TypeMirror erasedType = typeUtils.erasure(typeMirror);

        if (typeUtils.isSameType(erasedType, voidType)) {
            return VOID;
        }

        if (typeUtils.isSubtype(erasedType, numberType)) {
            return NUMBER;
        }

        if (typeUtils.isSubtype(erasedType, charSequenceType)) {
            return STRING;
        }

        if (typeUtils.isSameType(erasedType, dateType) || typeUtils.isSameType(erasedType, instantType)) {
            return TIMESTAMP;
        }

        if (typeUtils.isSameType(erasedType, booleanType)) {
            return BOOLEAN;
        }

        if (typeUtils.isSubtype(erasedType, mapType)) {
            return DYNAMIC;
        }

        if (typeUtils.isSameType(erasedType, multiPartFileType)) {
            return FILE;
        }

        return null;
    }
}
