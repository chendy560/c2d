package com.chendayu.dydoc.processor;

/**
 * 类似于 class 的东西
 */
public interface Declaration {

    Declaration STRING = () -> DeclarationType.STRING;

    Declaration NUMBER = () -> DeclarationType.NUMBER;

    Declaration TIMESTAMP = () -> DeclarationType.TIMESTAMP;

    Declaration BOOLEAN = () -> DeclarationType.BOOLEAN;

    Declaration ENUM_CONST = () -> DeclarationType.ENUM_CONST;

    Declaration DYNAMIC = () -> DeclarationType.DYNAMIC;

    Declaration ENUM = () -> DeclarationType.ENUM;

    Declaration VOID = () -> DeclarationType.VOID;

    Declaration TYPE_PARAMETER = () -> DeclarationType.TYPE_PARAMETER;

    static ArrayDeclaration arrayOf(Declaration declaration) {
        return () -> declaration;
    }

    static TypeArgDeclaration typeArgOf(String name) {
        return () -> name;
    }

    DeclarationType getType();

    interface ArrayDeclaration extends Declaration {

        @Override
        default DeclarationType getType() {
            return DeclarationType.ARRAY;
        }

        Declaration getComponentType();
    }

    interface TypeArgDeclaration extends Declaration {

        @Override
        default DeclarationType getType() {
            return DeclarationType.ARRAY;
        }

        String getName();
    }
}
