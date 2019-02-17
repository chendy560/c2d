package com.chendayu.dydoc.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleProcessor extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATIONS =
            Stream.of("org.springframework.stereotype.Controller",
                    "org.springframework.web.bind.annotation.RestController"
            ).collect(Collectors.toSet());

    private Messager messager;

    private ApiInfoStore apiInfoStore;

    private ResourceExtractor resourceExtractor;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATIONS;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.apiInfoStore = new ApiInfoStore(processingEnv);
        this.resourceExtractor = new ResourceExtractor(processingEnv, apiInfoStore);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (final TypeElement annotation : annotations) {
                for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    // 因为 Controller 和 RestController 都只能放在类上，所以这里可以无伤强转
                    resourceExtractor.getAndSave((TypeElement) element);
                }
            }

            if (roundEnv.processingOver()) {
                apiInfoStore.write();
            }

        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return false;
    }
}
