package com.chendayu.dydoc.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final DeclaredType declarationType;

    private List<Property> typeParameters;

    private List<ObjectProperty> properties;

    private Map<String, ObjectProperty> propertyMap;

    private List<Parent> parents;

    private Map<String, VariableElement> fieldMap;

    private List<ExecutableElement> getters;

    public ObjectDeclaration(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.qualifiedName = typeElement.getQualifiedName().toString();
        this.declarationType = ((DeclaredType) typeElement.asType());
    }

    public Collection<VariableElement> getFields() {
        return fieldMap.values();
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.OBJECT;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public boolean containsProperty(String name) {
        return propertyMap.containsKey(name);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }

    public void setTypeParameters(List<Property> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public void setProperties(List<ObjectProperty> properties) {
        this.properties = properties;
        if (!properties.isEmpty()) {
            propertyMap = new HashMap<>(properties.size() * 2);
            for (ObjectProperty property : properties) {
                propertyMap.put(property.getName(), property);
            }
        }
    }

    public void setFieldMap(Map<String, VariableElement> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public void setGetters(List<ExecutableElement> getters) {
        this.getters = getters;
    }

    public static class Parent {

        private final Declaration[] typeArgs;

        private final Declaration declaration;

        public Parent(Declaration[] typeArgs, Declaration declaration) {
            this.typeArgs = typeArgs;
            this.declaration = declaration;
        }
    }
}
