package com.chendayu.c2d.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.output.DocGenerator;
import com.chendayu.c2d.processor.resource.ResourceExtractor;

/**
 * 入口类
 * 这里没有用注解的方式定义支持的注解和源代码版本，目的很简单：假装能够挤出一点点性能
 */
public class SpringWebAnnotationProcessor extends AbstractProcessor {

    /**
     * spring 的 Controller 注解（在 spring-context 包里）
     */
    private static final String CONTROLLER = "org.springframework.stereotype.Controller";

    /**
     * spring 的 RestController 注解（在 spring-web 包里）
     */
    private static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";

    /**
     * spring-boot 的 SpringBootApplication 注解
     */
    private static final String SPRING_BOOT_APPLICATION = "org.springframework.boot.autoconfigure.SpringBootApplication";

    private static final Set<String> SUPPORTED_ANNOTATIONS =
            Stream.of(
                    CONTROLLER,
                    REST_CONTROLLER,
                    SPRING_BOOT_APPLICATION
            ).collect(Collectors.toSet());

    /**
     * 数据仓库
     */
    private Warehouse warehouse;

    /**
     * 资源数据（api数据）提取器
     */
    private ResourceExtractor resourceExtractor;

    /**
     * 应用信息提取器
     */
    private ApplicationMetaExtractor applicationMetaExtractor;

    /**
     * 文档生成器
     */
    private DocGenerator docGenerator;

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
        this.applicationMetaExtractor = new ApplicationMetaExtractor(processingEnv, warehouse);
        this.docGenerator = new DocGenerator(processingEnv);

        DocComment.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement annotation : annotations) {

                Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(annotation);

                if (annotation.getQualifiedName().toString().equals(SPRING_BOOT_APPLICATION)) {
                    for (Element element : annotated) {
                        applicationMetaExtractor.extract((TypeElement) element);
                    }
                    continue;
                }

                for (Element element : annotated) {
                    resourceExtractor.extract((TypeElement) element);
                }
            }

            if (roundEnv.processingOver()) {
                docGenerator.printDoc(warehouse);
            }

        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "no message");
            }
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
