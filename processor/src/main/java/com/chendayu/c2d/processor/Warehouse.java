package com.chendayu.c2d.processor;

import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.ObjectDeclaration;
import com.chendayu.c2d.processor.resource.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Warehouse {

    private static final String PACKAGE_NAME = "c2d";

    private final Map<String, ObjectDeclaration> declarationMap = new HashMap<>(16, 0.5f);

    private final Map<String, EnumDeclaration> enumMap = new HashMap<>(16, 0.5f);

    private final SortedMap<String, Resource> resourceMap = new TreeMap<>();

    private String applicationName = "Application";

    private String basePackage = PACKAGE_NAME;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage + "." + PACKAGE_NAME;
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
