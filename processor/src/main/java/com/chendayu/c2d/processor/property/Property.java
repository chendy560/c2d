package com.chendayu.c2d.processor.property;

import com.chendayu.c2d.processor.declaration.Declaration;

import java.util.List;

/**
 * 叫 property 的东西一般是 Java Bean 的一个 field
 * 但是为了方便，这里也指一个方法参数或者返回值
 * 其中方法返回值的 displayName 和 name 都是 null
 */
public class Property {

    /**
     * 名称，通常是字段名字或者参数名字
     */
    private final String name;

    /**
     * 显示名称，初始值于 name 相同，可能也可以被修改
     */
    private String displayName;

    /**
     * 描述，通常是字段或者参数的注释
     */
    private List<String> description;

    /**
     * 字段的类型
     */
    private Declaration declaration;

    /**
     * 没有名字的字段，通常是返回值
     */
    public Property(List<String> description, Declaration declaration) {
        this(null, description, declaration);
    }

    public Property(String name, List<String> description, Declaration declaration) {
        this.name = name;
        this.displayName = name;
        this.description = description;
        this.declaration = declaration;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
