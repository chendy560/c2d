package com.chendayu.dydoc.processor.declaration;


import com.chendayu.dydoc.processor.DeclarationExtractor;
import com.chendayu.dydoc.processor.Warehouse;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DeclarationTestProcessor extends AbstractProcessor {

    private Warehouse warehouse;

    private DeclarationExtractor declarationExtractor;

    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.warehouse = new Warehouse();
        this.declarationExtractor = new DeclarationExtractor(processingEnv, warehouse);
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DeclarationTest.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

                if (element.getKind() != ElementKind.CLASS) {
                    continue;
                }

                List<? extends Element> members = elementUtils.getAllMembers((TypeElement) element);
                for (Element member : members) {
                    if (member.getKind() == ElementKind.METHOD &&
                            member.getAnnotation(DeclarationTest.class) != null) {
                        ExecutableElement method = (ExecutableElement) member;
                        for (VariableElement parameter : method.getParameters()) {
                            declarationExtractor.extractAndSave(parameter);
                        }
                        TypeMirror returnType = method.getReturnType();
                        declarationExtractor.extractAndSave(returnType);
                    }
                }
            }
        }
        return false;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}
