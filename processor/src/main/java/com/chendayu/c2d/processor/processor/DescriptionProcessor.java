package com.chendayu.c2d.processor.processor;

import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.property.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class DescriptionProcessor extends AbstractNestedDeclarationPostProcessor {

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
            List<String> description = DocComment.create(getter).getReturn();
            if (!description.isEmpty()) {
                property.setDescription(description);
            }
        }

        VariableElement field = property.getField();
        if (field != null) {
            List<String> description = DocComment.create(field).getReturn();
            if (!description.isEmpty()) {
                property.setDescription(description);
            }
        }
    }

    @Override
    public int getOrder() {
        return lowestOrder();
    }
}
