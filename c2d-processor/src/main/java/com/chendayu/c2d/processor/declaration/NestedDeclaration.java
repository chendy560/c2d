package com.chendayu.c2d.processor.declaration;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.property.Comment;
import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.util.NameConversions;

public class NestedDeclaration implements Declaration {

    private TypeElement typeElement;

    private String shortName;

    private String qualifiedName;

    private String link;

    private String description = "";

    private LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

    private List<TypeVarDeclaration> typeParameters = Collections.emptyList();

    private List<Declaration> typeArguments = Collections.emptyList();

    private Set<NestedDeclaration> usedInDeclaration = Collections.emptySet();

    private Set<Action> usedInAction = Collections.emptySet();

    public NestedDeclaration(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.shortName = typeElement.getSimpleName().toString();
        this.qualifiedName = typeElement.getQualifiedName().toString();
        this.link = NameConversions.componentObjectLink(qualifiedName);
        this.description = Comment.create(typeElement).getCommentText();
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

    public String getLink() {
        return link;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public List<TypeVarDeclaration> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(List<TypeVarDeclaration> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public List<Declaration> getTypeArguments() {
        return typeArguments;
    }

    public Collection<Property> allProperties() {
        return propertyMap.values();
    }

    public Collection<Property> accessibleProperties() {
        List<Property> list = new ArrayList<>();
        for (Property p : propertyMap.values()) {
            if (!p.isIgnored() && p.isGettable()) {
                list.add(p);
            }
        }
        return list;
    }

    public void usedBy(Action action) {
        if (usedInAction.isEmpty()) {
            usedInAction = new HashSet<>();
        }
        this.usedInAction.add(action);
    }

    public Set<NestedDeclaration> getUsedInDeclaration() {
        return usedInDeclaration;
    }

    public Set<Action> getUsedInAction() {
        return usedInAction;
    }

    public void usedBy(NestedDeclaration nestedDeclaration) {
        if (usedInDeclaration.isEmpty()) {
            usedInDeclaration = new HashSet<>();
        }
        this.usedInDeclaration.add(nestedDeclaration);
    }

    public NestedDeclaration withTypeArguments(List<Declaration> typeArguments) {
        if (typeArguments.size() != typeParameters.size()) {
            throw new IllegalArgumentException("cannot set type arguments for " + qualifiedName +
                    " : expect <" + typeParameters.size() + "> type arguments but get <" +
                    typeArguments.size() + ">");
        }
        NestedDeclaration copy = copy();
        copy.typeArguments = typeArguments;
        copy.replaceTypeParameter();
        return copy;
    }

    private void replaceTypeParameter() {
        for (int i = 0; i < typeParameters.size(); i++) {
            TypeVarDeclaration typeParameter = typeParameters.get(i);
            Declaration typeArgument = typeArguments.get(i);

            for (Map.Entry<String, Property> entry : propertyMap.entrySet()) {
                Property property = entry.getValue();
                switch (property.getType()) {
                    case ARRAY:
                        ArrayDeclaration arrayDeclaration = (ArrayDeclaration) property.getDeclaration();
                        Declaration itemType = arrayDeclaration.getItemType();
                        if (typeParameter.equals(itemType)) {
                            Property copy = property.copy();
                            copy.setDeclaration(ArrayDeclaration.arrayOf(typeArgument));
                            entry.setValue(copy);
                        }
                        break;
                    case TYPE_PARAMETER:
                        Declaration declaration = property.getDeclaration();
                        if (typeParameter.equals(declaration)) {
                            Property copy = property.copy();
                            copy.setDeclaration(typeArgument);
                            entry.setValue(copy);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
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
        if (typeElement == null) {
            return null;
        }

        return typeElement.getAnnotation(clazz);
    }

    public boolean isUsed() {
        return !this.usedInAction.isEmpty() || !this.usedInDeclaration.isEmpty();
    }

    private NestedDeclaration copy() {
        NestedDeclaration copy = new NestedDeclaration();
        copy.typeElement = this.typeElement;
        copy.shortName = this.shortName;
        copy.qualifiedName = this.qualifiedName;
        copy.link = this.link;
        copy.description = this.description;
        copy.propertyMap = new LinkedHashMap<>(this.propertyMap);
        copy.typeParameters = new ArrayList<>(this.typeParameters);
        copy.typeArguments = new ArrayList<>(this.typeParameters);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NestedDeclaration that = (NestedDeclaration) o;
        return Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
