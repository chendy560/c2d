package com.chendayu.dydoc.processor;

import java.util.List;

public class ArrayDeclaration extends Declaration {

    private final Declaration componentType;

    public ArrayDeclaration(List<String> description, Declaration componentType) {
        super(description);
        this.componentType = componentType;
    }

    public ArrayDeclaration(String name, List<String> description, Declaration componentType) {
        super(name, description);
        this.componentType = componentType;
    }

    @Override
    public ParamType getType() {
        return ParamType.ARRAY;
    }

    public Declaration getComponentType() {
        return componentType;
    }
}
