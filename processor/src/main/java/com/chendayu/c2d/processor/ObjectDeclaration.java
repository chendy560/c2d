package com.chendayu.c2d.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final List<ObjectProperty> properties = new ArrayList<>();

    private final Map<String, ObjectProperty> propertyMap = new HashMap<>();

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

    public boolean containsProperty(String name) {
        return propertyMap.containsKey(name);
    }

    public String getQualifiedName() {
        return qualifiedName;
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

    // todo 不深不浅的拷贝，应该根据实际情况调整
    public ObjectDeclaration withTypeArgs(List<Declaration> typeArgs) {
        ObjectDeclaration copy = new ObjectDeclaration(typeElement);
        copy.properties.addAll(properties);
        copy.propertyMap.putAll(propertyMap);
        copy.setDescription(new ArrayList<>(description));
        copy.typeArgs = typeArgs;
        copy.typeParameters = new ArrayList<>(this.typeParameters);
        copy.fieldMap = new HashMap<>(fieldMap);
        return copy;
    }
}
