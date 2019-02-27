package com.chendayu.c2d.processor;

import java.util.List;

public class Property {

    private String name;

    private List<String> description;

    private Declaration declaration;

    public Property(List<String> description, Declaration declaration) {
        this(null, description, declaration);
    }

    public Property(String name, List<String> description, Declaration declaration) {
        this.name = name;
        this.description = description;
        this.declaration = declaration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public boolean descriptionIsEmpty() {
        return description == null || description.isEmpty();
    }
}
