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
    private String originName;

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

    private Property() {
        // for copy use
    }

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

    public Property mergeChild(Property child) {
        Property parent = this; // 更易读一点

        Property newProperty = new Property(parent.getOriginName(), parent.getDeclaration());
        newProperty.setDisplayName(parent.getDisplayName());
        newProperty.setDeclaration(child.getDeclaration());

        List<String> newDescription = child.getDescription();
        if (!newDescription.isEmpty()) {
            newProperty.setDescription(newDescription);
        } else {
            newProperty.setDescription(parent.description);
        }

        newProperty.setIgnored(parent.isIgnored());
        newProperty.setSettable(parent.isSettable() || child.isSettable());
        newProperty.setSetter(child.getSetter());

        newProperty.setGettable(parent.isGettable() || child.isGettable());
        newProperty.setGetter(child.getGetter());

        newProperty.setField(child.getField());

        return newProperty;
    }

    public Property copy() {
        Property copy = new Property();
        copy.originName = this.originName;
        copy.displayName = this.displayName;
        copy.description = this.description;
        copy.declaration = this.declaration;
        copy.ignored = this.ignored;
        copy.settable = this.settable;
        copy.gettable = this.gettable;
        copy.field = this.field;
        copy.getter = this.getter;
        copy.setter = this.setter;
        return copy;
    }
}
