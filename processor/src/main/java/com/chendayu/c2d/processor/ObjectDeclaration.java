package com.chendayu.c2d.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final List<ObjectProperty> properties = new ArrayList<>();

    private final Map<String, ObjectProperty> propertyMap = new HashMap<>();

    private List<String> description;

    private Property[] typeArgs;

    private Property[] typeParameters;

    private Map<String, VariableElement> fieldMap = new HashMap<>();

    private List<ExecutableElement> getters;

    public ObjectDeclaration(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.qualifiedName = typeElement.getQualifiedName().toString();
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

    public void setTypeParameters(Property[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public int indexOfTypeParameters(String name) {
        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void addProperty(ObjectProperty property) {
        String name = property.getName();
        if (this.containsProperty(name)) {
            throw new IllegalArgumentException("property '" + name + "' already exists in '" + name + "'");
        }
        this.properties.add(property);
        this.propertyMap.put(name, property);
    }

    public void addPropertyDescriptionIfNotExists(Property property) {
        String name = property.getName();
        ObjectProperty oldProperty = this.propertyMap.get(name);
        if (oldProperty == null) {
            throw new IllegalArgumentException("property '" + name + "' not exists in declaration '"
                    + qualifiedName + '\'');
        }

        if (oldProperty.descriptionIsEmpty()) {
            oldProperty.setDescription(property.getDescription());
        }
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
}
