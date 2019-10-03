package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collection;

import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

public class MarkUsageProcessor extends AbstractNestedDeclarationProcessor {

    public MarkUsageProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
        Collection<Property> accessibleProperties = nestedDeclaration.accessibleProperties();
        for (Property property : accessibleProperties) {
            Declaration declaration = property.getDeclaration();
            switch (declaration.getType()) {
                case ARRAY:
                    ArrayDeclaration arrayDeclaration = (ArrayDeclaration) declaration;
                    Declaration itemType = arrayDeclaration.getFinalItemType();
                    if (itemType.getType() == DeclarationType.OBJECT) {
                        NestedDeclaration nestedItem = (NestedDeclaration) itemType;
                        nestedItem.usedBy(nestedDeclaration);
                    }
                    break;
                case OBJECT:
                    NestedDeclaration nested = (NestedDeclaration) declaration;
                    if (!nested.equals(nestedDeclaration)) {
                        nestedDeclaration.usedBy(nested);
                    }
                    break;
                case ENUM:
                    EnumDeclaration enumDeclaration = (EnumDeclaration) declaration;
                    enumDeclaration.usedBy(nestedDeclaration);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getOrder() {
        return lowestOrder();
    }
}
