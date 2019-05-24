package com.chendayu.c2d.processor.declaration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TypeVarDeclaration implements Declaration {

    private final String name;

    private final List<String> description;

    public TypeVarDeclaration(String name) {
        this.name = name;
        this.description = Collections.singletonList("type var: " + name);
    }

    public TypeVarDeclaration(String name, List<String> description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.TYPE_PARAMETER;
    }

    @Override
    public List<String> getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeVarDeclaration that = (TypeVarDeclaration) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
