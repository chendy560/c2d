package com.chendayu.dydoc.processor;

import java.util.List;

/**
 * 简单值的声明，也就是除了数组和对象之外的东西
 */
public class SimpleValueDeclaration extends Declaration {

    private final ParamType type;

    private SimpleValueDeclaration(String name, List<String> description, ParamType type) {
        super(name, description);
        this.type = type;
    }

    private SimpleValueDeclaration(List<String> description, ParamType type) {
        super(description);
        this.type = type;
    }

    public static SimpleValueDeclaration stringParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.STRING);
    }

    public static SimpleValueDeclaration stringParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.STRING);
    }

    public static SimpleValueDeclaration numberParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.NUMBER);
    }

    public static SimpleValueDeclaration numberParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.NUMBER);
    }

    public static SimpleValueDeclaration timestampParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.TIMESTAMP);
    }

    public static SimpleValueDeclaration timestampParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.TIMESTAMP);
    }

    public static SimpleValueDeclaration booleanParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.BOOLEAN);
    }

    public static SimpleValueDeclaration booleanParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.BOOLEAN);
    }

    public static SimpleValueDeclaration enumConstParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.ENUM_CONST);
    }

    public static SimpleValueDeclaration enumConstParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.ENUM_CONST);
    }

    public static SimpleValueDeclaration dynamicParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.DYNAMIC_OBJECT);
    }

    public static SimpleValueDeclaration dynamicParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.DYNAMIC_OBJECT);
    }

    public static SimpleValueDeclaration typeParam(String name, List<String> description) {
        return new SimpleValueDeclaration(name, description, ParamType.TYPE_PARAMETER);
    }

    public static SimpleValueDeclaration typeParam(List<String> description) {
        return new SimpleValueDeclaration(description, ParamType.TYPE_PARAMETER);
    }

    @Override
    public ParamType getType() {
        return type;
    }
}
