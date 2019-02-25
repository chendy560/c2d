package com.chendayu.dydoc.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class ObjectProperty extends Property {

    private final VariableElement field;

    private final ExecutableElement getter;

    public ObjectProperty(String name, List<String> description, Declaration declaration,
                          VariableElement field, ExecutableElement getter) {
        super(name, description, declaration);
        this.field = field;
        this.getter = getter;
    }

    public VariableElement getField() {
        return field;
    }

    public ExecutableElement getGetter() {
        return getter;
    }
}
