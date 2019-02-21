package com.chendayu.dydoc.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

class ParameterTypeHelper {

    private final Types types;

    private final TypeMirror charSequenceType;

    private final TypeMirror numberType;

    private final TypeMirror booleanType;

    private final TypeMirror dateType;
    private final TypeMirror instantType;

    private final TypeMirror collectionType;
    private final TypeMirror mapType;
    private final TypeMirror enumType;

    ParameterTypeHelper(ProcessingEnvironment processEnv) {
        this.types = processEnv.getTypeUtils();
        Elements elements = processEnv.getElementUtils();

        this.charSequenceType = elements.getTypeElement(CharSequence.class.getName()).asType();

        this.booleanType = elements.getTypeElement(Boolean.class.getName()).asType();

        this.numberType = elements.getTypeElement(Number.class.getName()).asType();

        this.dateType = elements.getTypeElement(Date.class.getName()).asType();
        this.instantType = elements.getTypeElement(Instant.class.getName()).asType();
        this.collectionType = types.erasure(elements.getTypeElement(Collection.class.getName()).asType());
        this.mapType = types.erasure(elements.getTypeElement(Map.class.getName()).asType());

        this.enumType = types.erasure(elements.getTypeElement(Enum.class.getName()).asType());
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
                throw new IllegalArgumentException("unsupported type: " + typeMirror.toString());
        }
    }

    ParameterType findType(DeclaredType declaredType) {

        TypeMirror type = types.erasure(declaredType);

        if (types.isSubtype(type, numberType)) {
            return ParameterType.NUMBER;
        }

        if (types.isSubtype(type, charSequenceType)) {
            return ParameterType.STRING;
        }

        if (types.isSameType(type, dateType)) {
            return ParameterType.TIMESTAMP;
        }
        if (types.isSameType(type, instantType)) {
            return ParameterType.TIMESTAMP;
        }

        if (types.isSameType(type, booleanType)) {
            return ParameterType.BOOLEAN;
        }

        if (types.isSubtype(type, collectionType)) {
            return ParameterType.ARRAY;
        }

        if (types.isSubtype(type, mapType)) {
            return ParameterType.DYNAMIC_OBJECT;
        }

        if (types.isSubtype(type, enumType)) {
            return ParameterType.ENUM;
        }

        return ParameterType.OBJECT;
    }
}
