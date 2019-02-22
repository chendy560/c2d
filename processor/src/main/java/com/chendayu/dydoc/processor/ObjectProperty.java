package com.chendayu.dydoc.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class ObjectProperty extends Property {

    private final VariableElement field;

    private final ExecutableElement getter;

    public ObjectProperty(VariableElement field, ExecutableElement getter) {
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
