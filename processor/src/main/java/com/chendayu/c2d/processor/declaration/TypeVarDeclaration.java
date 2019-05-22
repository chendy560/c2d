package com.chendayu.c2d.processor.declaration;

public class TypeVarDeclaration implements Declaration {

    private final String name;

    public TypeVarDeclaration(String name) {
        this.name = name;
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.TYPE_PARAMETER;
    }

    public String getName() {
        return name;
    }
}
