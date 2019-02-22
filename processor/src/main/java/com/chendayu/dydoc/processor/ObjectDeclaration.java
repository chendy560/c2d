package com.chendayu.dydoc.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;

public class ObjectDeclaration implements Declaration {

    private String qualifiedName;

    private ObjectDeclaration superClass;

    private List<ObjectDeclaration> interfaces;

    private List<Declaration> typeArgs;

    private List<Property> properties;

    private TypeElement clazzElement;

    private Map<String, VariableElement> fields;

    private Map<String, ExecutableElement> getters;

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    public void initFieldAndGetters() {

    }


    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public ObjectDeclaration getSuperClass() {
        return superClass;
    }

    public void setSuperClass(ObjectDeclaration superClass) {
        this.superClass = superClass;
    }

    public List<ObjectDeclaration> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<ObjectDeclaration> interfaces) {
        this.interfaces = interfaces;
    }

    public List<Declaration> getTypeArgs() {
        return typeArgs;
    }

    public void setTypeArgs(List<Declaration> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public TypeElement getClazzElement() {
        return clazzElement;
    }

    public void setClazzElement(TypeElement clazzElement) {
        this.clazzElement = clazzElement;
    }

    public Map<String, VariableElement> getFields() {
        return fields;
    }

    public Map<String, ExecutableElement> getGetters() {
        return getters;
    }

    public void setGetters(Map<String, ExecutableElement> getters) {
        this.getters = getters;
    }

}
