package com.chendayu.dydoc.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({
        "org.springframework.stereotype.Controller",
        "org.springframework.web.bind.annotation.RestController"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleProcessor extends AbstractProcessor {

    private Messager messager;

    private ApiInfoStore apiInfoStore;

    private ResourceExtractor resourceExtractor;

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
