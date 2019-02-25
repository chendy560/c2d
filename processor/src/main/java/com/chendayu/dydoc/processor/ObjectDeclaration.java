package com.chendayu.dydoc.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;
import java.util.Map;

public class ObjectDeclaration implements Declaration {

    private final TypeElement typeElement;

    private final String qualifiedName;

    private final DeclaredType declarationType;

    private List<Property> typeParameters;

    private List<Property> properties;

    private List<Parent> parents;

    private Map<String, VariableElement> fields;

    private Map<String, ExecutableElement> getters;

    public ObjectDeclaration(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.qualifiedName = typeElement.getQualifiedName().toString();
        this.declarationType = ((DeclaredType) typeElement.asType());
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.OBJECT;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }

    public void setTypeParameters(List<Property> typeParameters) {
        this.typeParameters = typeParameters;
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
