package com.chendayu.c2d.processor.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Comment;
import com.chendayu.c2d.processor.property.Property;

public class DescriptionProcessor extends AbstractNestedDeclarationProcessor {

    public DescriptionProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(NestedDeclaration nestedDeclaration) {
        nestedDeclaration.allProperties().forEach(this::initDescription);
    }

    private void initDescription(Property property) {
        ExecutableElement getter = property.getGetter();
        if (getter != null) {
            String description = Comment.create(getter).getReturnText();
            if (!description.isEmpty()) {
                property.setDescription(description);
                return;
            }
        }

        VariableElement field = property.getField();
        if (field != null) {
            Comment comment = Comment.create(field);
            property.setComment(comment);
        }
    }

    @Override
    public int getOrder() {
        return lowestOrder();
    }
}
