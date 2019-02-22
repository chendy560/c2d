package com.chendayu.dydoc.processor;

public class ArrayDeclaration implements Declaration {

    private final Declaration componentType;

    public ArrayDeclaration(Declaration componentType) {
        this.componentType = componentType;
    }

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    public Declaration getComponentType() {
        return componentType;
    }
}
