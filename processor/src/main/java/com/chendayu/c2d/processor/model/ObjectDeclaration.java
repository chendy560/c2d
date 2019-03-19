package com.chendayu.c2d.processor.model;

import com.chendayu.c2d.processor.Utils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final String name;

    private final String hash;

    private final LinkedHashMap<String, ObjectProperty> propertyMap = new LinkedHashMap<>();

    private List<String> description;

    private List<Declaration> typeArgs = Collections.emptyList();

    private List<Property> typeParameters = Collections.emptyList();

    private Map<String, VariableElement> fieldMap = new HashMap<>();

    public ObjectDeclaration(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.qualifiedName = typeElement.getQualifiedName().toString();
        this.name = typeElement.getSimpleName().toString();
        this.hash = "d" + Utils.shortHash(qualifiedName);
    }

    public Collection<VariableElement> getFields() {
        return fieldMap.values();
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.OBJECT;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
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
        String innerName = property.getName();
        if (this.containsProperty(innerName)) {
            throw new IllegalArgumentException("property '" + innerName + "' already exists in '" + innerName + "'");
        }
        this.propertyMap.put(innerName, property);
    }

    public void addPropertyDescriptionIfNotExists(Property property) {
        String innerName = property.getName();
        ObjectProperty oldProperty = this.propertyMap.get(innerName);
        if (oldProperty == null) {
            throw new IllegalArgumentException("property '" + innerName + "' not exists in declaration '"
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
        objectProperty.setDisplayName(to);
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
