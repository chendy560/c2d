package com.chendayu.dydoc.processor;

import java.util.Collections;
import java.util.List;

class Parameter {

    private final String name;

    private ParameterType type;

    private String objectName;

    private String objectHash;

    private List<String> description = Collections.emptyList();

    Parameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
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

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
