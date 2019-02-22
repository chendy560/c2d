package com.chendayu.dydoc.processor;

import java.util.List;

public class ObjectDeclaration extends Declaration {

    private List<Declaration> typeArgs;

    private List<Declaration> properties;

    public ObjectDeclaration(List<String> description) {
        super(description);
    }

    public ObjectDeclaration(String name, List<String> description) {
        super(name, description);
    }

    @Override
    public ParamType getType() {
        return ParamType.OBJECT;
    }

    public List<Declaration> getTypeArgs() {
        return typeArgs;
    }

    public void setTypeArgs(List<Declaration> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public List<Declaration> getProperties() {
        return properties;
    }

    public void setProperties(List<Declaration> properties) {
        this.properties = properties;
    }
}
