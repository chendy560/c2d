package com.chendayu.c2d.processor;

import java.util.List;

/**
 * 叫 property 的东西一般是 Java Bean 的一个 field
 * 但是为了方便，这里也指一个方法参数或者返回值
 * 其中方法返回值的 name 和 originName 都是 null
 */
public class Property {

    /**
     * 原始名称，因为一些场景中字段会被改名，但是一些逻辑又需要用到本来的字段名，所以保留了下来
     */
    private final String originName;

    /**
     * 名称，最初和 originName 相同，可能会被修改
     */
    private String name;

    /**
     * 描述，通常是字段或者参数的注释
     */
    private List<String> description;

    /**
     * 字段的类型
     */
    private Declaration declaration;

    public Property(List<String> description, Declaration declaration) {
        this(null, description, declaration);
    }

    public Property(String name, List<String> description, Declaration declaration) {
        this.originName = name;
        this.name = name;
        this.description = description;
        this.declaration = declaration;
    }

    public String getOriginName() {
        return originName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public boolean descriptionIsEmpty() {
        return description == null || description.isEmpty();
    }
}
