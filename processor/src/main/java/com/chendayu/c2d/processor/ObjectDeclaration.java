package com.chendayu.c2d.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final LinkedHashMap<String, ObjectProperty> propertyMap = new LinkedHashMap<>();

    private List<String> description;

    private List<Declaration> typeArgs;

    private List<Property> typeParameters;

    private Map<String, VariableElement> fieldMap = new HashMap<>();

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

    public List<Declaration> getTypeArgs() {
        return typeArgs;
    }

    public List<Property> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(List<Property> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public boolean containsProperty(String name) {
        return propertyMap.containsKey(name);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void addProperty(ObjectProperty property) {
        String name = property.getName();
        if (this.containsProperty(name)) {
            throw new IllegalArgumentException("property '" + name + "' already exists in '" + name + "'");
        }
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

    public Collection<ObjectProperty> getProperties() {
        return propertyMap.values();
    }

    public List<ObjectProperty> copyProperties() {
        return new ArrayList<>(propertyMap.values());
    }

    public void setFieldMap(Map<String, VariableElement> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public void removeProperty(String name) {
        propertyMap.remove(name);
    }

    public void renameProperty(String from, String to) {
        if (propertyMap.containsKey(to)) {
            throw new IllegalArgumentException("failed rename property '" + from + "' to '"
                    + to + "' in declaration '" + qualifiedName + "' : property already exists");
        }
        ObjectProperty objectProperty = propertyMap.get(from);
        if (objectProperty == null) {
            throw new IllegalArgumentException("failed rename property '" + from + "' to '"
                    + to + "' in declaration '" + qualifiedName + "' : property not exists");
        }
        propertyMap.remove(from);
        objectProperty.setName(to);
        addProperty(objectProperty);
    }

    public ObjectDeclaration withTypeArgs(List<Declaration> typeArgs) {
        ObjectDeclaration copy = new ObjectDeclaration(typeElement);
        copy.propertyMap.putAll(propertyMap);
        copy.setDescription(new ArrayList<>(description));
        copy.typeArgs = typeArgs;
        copy.typeParameters = new ArrayList<>(this.typeParameters);
        copy.fieldMap = new HashMap<>(fieldMap);
        return copy;
    }
}
