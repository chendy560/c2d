package com.chendayu.dydoc.processor;

import java.util.List;

class Parameter extends DocElement {

    private static final String PREFIX = "p";

    private ParameterType type;

    private List<Parameter> typeArgs;

    private String objectName;

    private String objectHash;

    Parameter(String name) {
        super(name);
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public List<Parameter> getTypeArgs() {
        return typeArgs;
    }

    public void setTypeArgs(List<Parameter> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectHash() {
        return objectHash;
    }

    public void setObjectHash(String objectHash) {
        this.objectHash = objectHash;
    }
}
