package com.chendayu.c2d.processor.property;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;

/**
 * 叫 property 的东西一般是 Java Bean 的一个 field
 * 但是为了方便，这里也指一个方法参数或者返回值
 * 其中方法返回值的 displayName 和 originName 都是 null
 */
public class Property {

    /**
     * 字段名称 / 参数名称，如果是返回值则为null
     */
    private final String originName;

    /**
     * 显示名称，默认与 originName 相同，可以被修改
     */
    private String displayName;

    /**
     * 字段 / 参数 / 返回值 上的注释
     */
    private List<String> description;

    /**
     * 类型
     */
    private Declaration declaration;

    /**
     * 是否被忽略
     */
    private boolean ignored;

    /**
     * 是否可以设置值，即是否可以作为参数传入
     */
    private boolean settable;

    /**
     * 是否可以获取值，即是否可以作为数据返回
     */
    private boolean gettable;

    /**
     * 当作为字段时的原始字段
     */
    private VariableElement field;

    /**
     * 当作为字段时的getter
     */
    private ExecutableElement getter;

    /**
     * 当作为字段时的setter
     */
    private ExecutableElement setter;

    /**
     * 没有名字的字段，即返回值
     */
    public Property(List<String> description, Declaration declaration) {
        this(null, description, declaration);
    }

    public Property(String originName, Declaration declaration) {
        this(originName, Collections.emptyList(), declaration);
    }

    public Property(String originName, List<String> description, Declaration declaration) {
        this.originName = originName;
        this.displayName = originName;
        this.description = description;
        this.declaration = declaration;
    }

    public String getOriginName() {
        return originName;
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

    public boolean hasDescription() {
        return !this.description.isEmpty();
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

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public boolean isGettable() {
        return gettable;
    }

    public void setGettable(boolean gettable) {
        this.gettable = gettable;
    }

    public VariableElement getField() {
        return field;
    }

    public void setField(VariableElement field) {
        this.field = field;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public void setGetter(ExecutableElement getter) {
        this.getter = getter;
    }

    public ExecutableElement getSetter() {
        return setter;
    }

    public void setSetter(ExecutableElement setter) {
        this.setter = setter;
    }

    public DeclarationType getType() {
        return declaration.getType();
    }

    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        if (getter != null) {
            T annotation = getter.getAnnotation(clazz);
            if (annotation != null) {
                return annotation;
            }
        }

        if (setter != null) {
            T annotation = setter.getAnnotation(clazz);
            if (annotation != null) {
                return annotation;
            }
        }

        if (field != null) {
            return field.getAnnotation(clazz);
        }

        return null;
    }

    public Property apply(Property property) {
        Property newProperty = new Property(this.getOriginName(), this.getDeclaration());
        newProperty.setDisplayName(this.getDisplayName());
        newProperty.setDeclaration(property.getDeclaration());

        List<String> newDescription = property.getDescription();
        if (!newDescription.isEmpty()) {
            newProperty.setDescription(newDescription);
        } else {
            newProperty.setDescription(this.description);
        }

        newProperty.setIgnored(this.isIgnored());
        newProperty.setSettable(this.isSettable() || property.isSettable());
        newProperty.setSetter(property.getSetter());

        newProperty.setGettable(this.isGettable() || property.isGettable());
        newProperty.setGetter(property.getGetter());

        newProperty.setField(property.getField());

        return newProperty;
    }
}
