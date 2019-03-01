package com.chendayu.c2d.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringWebAnnotationProcessor extends AbstractProcessor {

    private static final String CONTROLLER = "org.springframework.stereotype.Controller";
    private static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    private static final String SPRING_BOOT_APPLICATION = "org.springframework.boot.autoconfigure.SpringBootApplication";

    private static final Set<String> SUPPORTED_ANNOTATIONS =
            Stream.of(
                    CONTROLLER,
                    REST_CONTROLLER,
                    SPRING_BOOT_APPLICATION
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
            for (TypeElement annotation : annotations) {

                Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(annotation);

                if (annotation.getQualifiedName().toString().equals(SPRING_BOOT_APPLICATION)) {
                    for (Element element : annotated) {
                        extractApplicationName(element);
                    }
                    continue;
                }

                for (Element element : annotated) {
                    // 因为 Controller 和 RestController 都只能放在类上，所以这里可以无伤强转
                    resourceExtractor.extract((TypeElement) element);
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

    private void extractApplicationName(Element element) {
        ElementKind kind = element.getKind();
        if (kind != ElementKind.CLASS) {
            return;
        }

        TypeElement typeElement = (TypeElement) element;
        String mainClassName = typeElement.getSimpleName().toString();
        int lastApplicationIndex = mainClassName.lastIndexOf("Application");
        if (lastApplicationIndex <= 0) {
            return;
        }

        String applicationName = mainClassName.substring(0, lastApplicationIndex);

        StringBuilder builder = new StringBuilder();
        builder.append(applicationName.charAt(0));

        for (int i = 1; i < applicationName.length(); i++) {
            char c = applicationName.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append(' ');
            }
            builder.append(c);
        }

        warehouse.setApplicationName(builder.toString());
    }

    /**
     * 用于为子类暴露 Warehouse 进行测试的方法
     */
    protected Warehouse getWarehouse() {
        return warehouse;
    }
}
