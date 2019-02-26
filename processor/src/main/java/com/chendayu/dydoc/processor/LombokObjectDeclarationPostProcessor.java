package com.chendayu.dydoc.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.List;

public class LombokObjectDeclarationPostProcessor extends AbstractObjectDeclarationPostProcessor {

    private static final String DATA_ANNOTATION = "lombok.Data";

    private static final String GETTER_ANNOTATION = "lombok.Getter";

    @Override
    public void process(ObjectDeclaration objectDeclaration) {
        TypeElement typeElement = objectDeclaration.getTypeElement();
        if (!hasDataOrGetter(typeElement)) {
            return;
        }

        Collection<VariableElement> fields = objectDeclaration.getFields();
        for (VariableElement field : fields) {

        }
    }

    private boolean hasDataOrGetter(TypeElement typeElement) {
        List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            String name = annotationElement.getQualifiedName().toString();
            if (name.equals(DATA_ANNOTATION) || name.equals(GETTER_ANNOTATION)) {
                return true;
            }
        }
        return false;
    }
}
