package com.chendayu.dydoc.processor;

import java.util.ArrayList;
import java.util.List;

public class ObjectStructure extends DocElement {

    private static final String PREFIX = "o";

    private final List<Parameter> parameters = new ArrayList<>();

    public ObjectStructure(String name) {
        super(name);
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
