package com.chendayu.c2d.processor;

public class Declarations {

    public static final Declaration STRING = () -> DeclarationType.STRING;
    public static final Declaration NUMBER = () -> DeclarationType.NUMBER;
    public static final Declaration TIMESTAMP = () -> DeclarationType.TIMESTAMP;
    public static final Declaration BOOLEAN = () -> DeclarationType.BOOLEAN;
    public static final Declaration ENUM_CONST = () -> DeclarationType.ENUM_CONST;
    public static final Declaration DYNAMIC = () -> DeclarationType.DYNAMIC;
    public static final Declaration ENUM = () -> DeclarationType.ENUM;
    public static final Declaration VOID = () -> DeclarationType.VOID;

    private Declarations() {

    }

    public static ArrayDeclaration arrayOf(Declaration declaration) {
        return () -> declaration;
    }

    public static TypeArgDeclaration typeArgOf(String name) {
        return () -> name;
    }

    public interface ArrayDeclaration extends Declaration {

        @Override
        default DeclarationType getType() {
            return DeclarationType.ARRAY;
        }

        Declaration getComponentType();
    }

    public interface TypeArgDeclaration extends Declaration {

        @Override
        default DeclarationType getType() {
            return DeclarationType.TYPE_PARAMETER;
        }

        String getName();
    }
}
