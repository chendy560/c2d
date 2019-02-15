package com.chendayu.dydoc.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.List;

class ParameterTypeHelper {

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

    private final TypeMirror list;

    ParameterTypeHelper(ProcessingEnvironment processEnv) {
        this.types = processEnv.getTypeUtils();
        Elements elements = processEnv.getElementUtils();

        this.string = elements.getTypeElement(String.class.getName()).asType();

        this.booleanType = elements.getTypeElement(Boolean.class.getName()).asType();

        this.integerType = elements.getTypeElement(Integer.class.getName()).asType();
        this.longType = elements.getTypeElement(Long.class.getName()).asType();
        this.doubleType = elements.getTypeElement(Double.class.getName()).asType();
        this.bigDecimal = elements.getTypeElement(BigDecimal.class.getName()).asType();
        this.bigInteger = elements.getTypeElement(BigInteger.class.getName()).asType();

        this.date = elements.getTypeElement(Date.class.getName()).asType();
        this.instant = elements.getTypeElement(Instant.class.getName()).asType();
        this.list = types.erasure(elements.getTypeElement(List.class.getName()).asType());
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

        if (types.isSameType(type, list)) {
            return ParameterType.ARRAY;
        }

        return ParameterType.OBJECT;
    }
}
