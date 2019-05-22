package com.chendayu.c2d.processor.declaration;

/**
 * 数组类型
 */
public class ArrayDeclaration implements Declaration {

    /**
     * 数组元素的类型
     */
    private final Declaration itemType;

    private ArrayDeclaration(Declaration itemType) {
        this.itemType = itemType;
    }

    public static ArrayDeclaration arrayOf(Declaration componentType) {
        return new ArrayDeclaration(componentType);
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.ARRAY;
    }

    public Declaration getItemType() {
        return itemType;
    }
}
