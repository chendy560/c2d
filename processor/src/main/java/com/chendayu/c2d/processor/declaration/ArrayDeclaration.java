package com.chendayu.c2d.processor.declaration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 数组类型
 */
public class ArrayDeclaration implements Declaration {

    /**
     * 数组元素的类型
     */
    private final Declaration itemType;

    private final List<String> description;

    private ArrayDeclaration(Declaration itemType) {
        this.itemType = itemType;
        this.description = Collections.singletonList("array of " + itemType.getDescription());
    }

    public static ArrayDeclaration arrayOf(Declaration componentType) {
        return new ArrayDeclaration(componentType);
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.ARRAY;
    }

    @Override
    public List<String> getDescription() {
        return description;
    }

    public Declaration getItemType() {
        return itemType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayDeclaration) {
            return Objects.equals(itemType, ((ArrayDeclaration) obj).itemType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return itemType.hashCode();
    }
}
