package com.chendayu.dydoc.processor;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static com.chendayu.dydoc.processor.Utils.findRequestMapping;

public class ResourceExtractor2 extends InfoExtractor {

    private static final int CONTROLLER_LENGTH = "Controller".length();

    private final ActionExtractor actionExtractor;

    public ResourceExtractor2(ProcessingEnvironment processEnv, ApiInfoStore store) {
        super(processEnv, store);
        this.actionExtractor = new ActionExtractor(processEnv, store);
    }

    public void getAndSave(TypeElement typeElement) {
        String resourceName = findResourceName(typeElement);
        if (store.containsResource(resourceName)) {
            messager.printMessage(Diagnostic.Kind.WARNING, "resource '" + resourceName + " already exists");
            return;
        }
        Resource resource = new Resource(resourceName);
        resource.setPath(getControllerPath(typeElement));

        elementUtils.getAllMembers(typeElement).stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> actionExtractor.findAction((ExecutableElement) e))
                .forEach(a -> {
                    if (a != null) {
                        resource.addAction(a);
                    }
                });

        store.addResource(resource);
    }

    /**
     * 其实就是把 XxController 的 Controller 剪掉了
     */
    private String findResourceName(TypeElement typeElement) {
        Name simpleName = typeElement.getSimpleName();
        return simpleName.subSequence(0, simpleName.length() - CONTROLLER_LENGTH).toString();
    }

    private String getControllerPath(TypeElement element) {
        RequestMapping requestMapping = element.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return findRequestMapping(requestMapping.value(), requestMapping.path());
        }
        return "";
    }
}
