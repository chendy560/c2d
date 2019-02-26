package com.chendayu.dydoc.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.List;

/**
 * 处理 lombok 的 Getter 和 Data 注解
 * 将所有的 field 作为 property 放进 declaration
 */
public class LombokObjectDeclarationPostProcessor extends AbstractObjectDeclarationPostProcessor {

    private static final String DATA_ANNOTATION = "lombok.Data";

    private static final String GETTER_ANNOTATION = "lombok.Getter";

    private final DeclarationExtractor declarationExtractor;

    public LombokObjectDeclarationPostProcessor(ProcessingEnvironment processingEnv, DeclarationExtractor declarationExtractor) {
        super(processingEnv);
        this.declarationExtractor = declarationExtractor;
    }

    @Override
    public void process(ObjectDeclaration objectDeclaration) {
        TypeElement typeElement = objectDeclaration.getTypeElement();
        if (!hasDataOrGetter(typeElement)) {
            return;
        }

        Collection<VariableElement> fields = objectDeclaration.getFields();
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            if (objectDeclaration.containsProperty(fieldName)) {
                continue;
            }

            Declaration declaration = declarationExtractor.extractAndSave(field);
            List<String> description = DocComment.create(elementUtils.getDocComment(field)).getDescription();
            ObjectProperty property = new ObjectProperty(fieldName, description, declaration, field, null);
            objectDeclaration.addProperty(property);
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

    @Override
    public int getOrder() {
        return highestOrder();
    }
}
