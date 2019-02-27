package com.chendayu.c2d.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringWebAnnotationProcessor extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATIONS =
            Stream.of(
                    "org.springframework.stereotype.Controller",
                    "org.springframework.web.bind.annotation.RestController"
            ).collect(Collectors.toSet());

    private Warehouse warehouse;

    private ResourceExtractor resourceExtractor;

    private DocGenerator docGenerator;

    private Messager messager;

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
        this.warehouse = new Warehouse();
        this.resourceExtractor = new ResourceExtractor(processingEnv, warehouse);
        this.docGenerator = new DocGenerator(processingEnv);
        this.messager = processingEnv.getMessager();
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
                docGenerator.printDoc(warehouse);
            }

        } catch (Exception e) {
            String message = e.getMessage();
            String trace = Arrays.toString(e.getStackTrace());
            messager.printMessage(Diagnostic.Kind.ERROR, message + trace);
        }
        return false;
    }

    /**
     * 用于为子类暴露 Warehouse 进行测试的方法
     */
    protected Warehouse getWarehouse() {
        return warehouse;
    }
}
