package com.chendayu.c2d.processor;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class Warehouse {

    private final SortedMap<String, ObjectDeclaration> declarationMap = new TreeMap<>();

    private final SortedMap<String, Resource> resourceMap = new TreeMap<>();

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
}
