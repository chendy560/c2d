package com.chendayu.dydoc.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.*;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final DeclaredType declarationType;

    private final List<ObjectProperty> properties = new ArrayList<>();

    private final Map<String, ObjectProperty> propertyMap = new HashMap<>();

    private List<String> description;

    private List<Property> typeParameters;

    private List<Parent> parents;

    private Map<String, VariableElement> fieldMap = new HashMap<>();

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

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
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

    public void addProperty(ObjectProperty property) {
        String name = property.getName();
        if (this.containsProperty(name)) {
            throw new IllegalArgumentException("property '" + name + "' already exists in '" + name + "'");
        }
        this.properties.add(property);
        this.propertyMap.put(name, property);
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
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
