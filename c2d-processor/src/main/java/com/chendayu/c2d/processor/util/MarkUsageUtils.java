package com.chendayu.c2d.processor.util;

import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;

public class MarkUsageUtils {

    private MarkUsageUtils() {

    }

    public static void markUsage(Declaration declaration, Action action) {
        DeclarationType type = declaration.getType();
        switch (type) {
            case OBJECT:
                NestedDeclaration nestedDeclaration = (NestedDeclaration) declaration;
                nestedDeclaration.usedBy(action);
                for (Declaration typeArgument : nestedDeclaration.getTypeArguments()) {
                    markUsage(typeArgument, action);
                }
                break;
            case ARRAY:
                ArrayDeclaration arrayDeclaration = (ArrayDeclaration) declaration;
                Declaration itemType = arrayDeclaration.getItemType();
                markUsage(itemType, action);
                break;
            default:
                break;
        }
    }
}
