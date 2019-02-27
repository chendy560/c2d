package com.chendayu.dydoc.processor;

//todo
public class GenericObjectDeclaration implements Declaration {

    private final Declaration[] typeArgs;

    private final String objectName;

    public GenericObjectDeclaration(Declaration[] typeArgs, String objectName) {
        this.typeArgs = typeArgs;
        this.objectName = objectName;
    }

    @Override
    public DeclarationType getType() {
        return null;
    }
}
