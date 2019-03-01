package com.chendayu.c2d.processor;

import java.util.*;

public class Warehouse {

    private final Map<String, ObjectDeclaration> declarationMap = new HashMap<>(16, 0.5f);

    private final Map<String, EnumDeclaration> enumMap = new HashMap<>(16, 0.5f);

    private final SortedMap<String, Resource> resourceMap = new TreeMap<>();

    private String applicationName;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public ObjectDeclaration getDeclaration(String qualifiedName) {
        return declarationMap.get(qualifiedName);
    }

    public void addDeclaration(ObjectDeclaration declaration) {
        declarationMap.put(declaration.getQualifiedName(), declaration);
    }

    public boolean containsResource(String name) {
        return resourceMap.containsKey(name);
    }

    public void addResource(Resource resource) {
        resourceMap.put(resource.getName(), resource);
    }

    public Collection<Resource> getResources() {
        return resourceMap.values();
    }

    public EnumDeclaration getEnumDeclaration(String qualifiedName) {
        return enumMap.get(qualifiedName);
    }

    public void addEnumDeclaration(EnumDeclaration enumDeclaration) {
        enumMap.put(enumDeclaration.getQualifiedName(), enumDeclaration);
    }
}
