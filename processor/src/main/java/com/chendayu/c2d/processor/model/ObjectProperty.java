package com.chendayu.c2d.processor.model;

import com.chendayu.c2d.processor.declaration.Declaration;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

/**
 * 对象中的字段，为了方便进一步的操作，携带了更多信息
 */
public class ObjectProperty extends Property {

    /**
     * 字段对应 field，字段没有 field（比如只有 getter）时为null
     */
    private final VariableElement field;

    /**
     * 字段对应 getter，字段没有 getter（比如通过lombok生成）时为null
     */
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
