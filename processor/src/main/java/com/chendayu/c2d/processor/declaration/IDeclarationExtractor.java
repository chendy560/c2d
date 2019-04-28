package com.chendayu.c2d.processor.declaration;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public interface IDeclarationExtractor {

    Declaration extract(TypeMirror typeMirror);

    default Declaration extract(VariableElement variableElement) {
        return extract(variableElement.asType());
    }
}
