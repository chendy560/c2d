package com.chendayu.dydoc.processor;

import java.util.ArrayList;
import java.util.List;

public class ObjectStruct {

    private final String name;

    private final String hash;

    private final List<Parameter> parameters = new ArrayList<>();

    private List<String> description;

    public ObjectStruct(String name) {
        this.name = name;
        this.hash = 'o' + Sha256.shortHash(name);
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
