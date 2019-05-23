package com.chendayu.c2d.processor.declaration;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.chendayu.c2d.processor.Utils;
import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.property.Property;

public class NestedDeclaration implements Declaration {

    private static final char HASH_PREFIX = 'd';

    private TypeElement typeElement;

    private String shortName;

    private String qualifiedName;

    private String hash;

    private List<String> description = Collections.emptyList();

    private LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

    private List<Property> typeParameters = Collections.emptyList();

    private List<Declaration> typeArguments = Collections.emptyList();

    public NestedDeclaration(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.shortName = typeElement.getSimpleName().toString();
        this.qualifiedName = typeElement.getQualifiedName().toString();
        this.hash = HASH_PREFIX + Utils.shortHash(qualifiedName);
        this.description = DocComment.create(typeElement).getDescription();
    }

    private NestedDeclaration() {
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.OBJECT;
    }

    public String getShortName() {
        return shortName;
    }

    public String getHash() {
        return hash;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<Property> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(List<Property> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public List<Declaration> getTypeArguments() {
        return typeArguments;
    }

    public Collection<Property> allProperties() {
        return propertyMap.values();
    }

    public Collection<Property> gettableProperties() {
        List<Property> list = new ArrayList<>();
        for (Property p : propertyMap.values()) {
            if (!p.isIgnored() && p.isGettable()) {
                list.add(p);
            }
        }
        return list;
    }

    public NestedDeclaration withTypeArguments(List<Declaration> typeArguments) {
        return new NestedDeclarationWithTypeArgs(this, typeArguments);
    }

    public void applyParent(NestedDeclaration declaration) {
        applyProperties(declaration.allProperties());
    }

    public void applyProperties(Collection<Property> properties) {
        if (this.propertyMap == null) {
            this.propertyMap = new LinkedHashMap<>();
        }

        for (Property property : properties) {
            String originName = property.getOriginName();
            Property oldProperty = propertyMap.get(originName);
            if (oldProperty != null) {
                propertyMap.put(originName, oldProperty.mergeChild(property));
            } else {
                propertyMap.put(originName, property);
            }
        }
    }

    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return typeElement.getAnnotation(clazz);
    }

    private static final class NestedDeclarationWithTypeArgs extends NestedDeclaration {

        private final NestedDeclaration nestedDeclaration;

        private final List<Declaration> typeArguments;

        private NestedDeclarationWithTypeArgs(NestedDeclaration nestedDeclaration, List<Declaration> typeArguments) {
            this.nestedDeclaration = nestedDeclaration;
            this.typeArguments = typeArguments;
        }

        @Override
        public DeclarationType getType() {
            return nestedDeclaration.getType();
        }

        @Override
        public String getShortName() {
            return nestedDeclaration.getShortName();
        }

        @Override
        public String getQualifiedName() {
            return nestedDeclaration.getQualifiedName();
        }

        @Override
        public String getHash() {
            return nestedDeclaration.getHash();
        }

        @Override
        public List<String> getDescription() {
            return nestedDeclaration.getDescription();
        }

        @Override
        public List<Property> getTypeParameters() {
            return nestedDeclaration.getTypeParameters();
        }

        @Override
        public void setTypeParameters(List<Property> typeParameters) {
            nestedDeclaration.setTypeParameters(typeParameters);
        }

        @Override
        public List<Declaration> getTypeArguments() {
            return typeArguments;
        }

        @Override
        public Collection<Property> allProperties() {
            return nestedDeclaration.allProperties();
        }

        @Override
        public NestedDeclaration withTypeArguments(List<Declaration> typeArguments) {
            return nestedDeclaration.withTypeArguments(typeArguments);
        }

        @Override
        public Collection<Property> gettableProperties() {
            return nestedDeclaration.gettableProperties();
        }

        @Override
        public void applyParent(NestedDeclaration declaration) {
            nestedDeclaration.applyParent(declaration);
        }

        @Override
        public void applyProperties(Collection<Property> properties) {
            nestedDeclaration.applyProperties(properties);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> clazz) {
            return nestedDeclaration.getAnnotation(clazz);
        }
    }
}
