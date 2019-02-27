package com.chendayu.c2d.processor;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.List;

import static com.chendayu.c2d.processor.Utils.findRequestMapping;

public class ResourceExtractor extends InfoExtractor {

    private final ActionExtractor actionExtractor;

    public ResourceExtractor(ProcessingEnvironment toolbox, Warehouse warehouse) {
        super(toolbox, warehouse);
        this.actionExtractor = new ActionExtractor(toolbox, warehouse);
    }

    public void getAndSave(TypeElement typeElement) {
        String resourceName = findResourceName(typeElement);
        if (warehouse.containsResource(resourceName)) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "resource '" + resourceName + " already exists");
            return;
        }

        Resource resource = new Resource(resourceName);
        resource.setPath(getControllerPath(typeElement));

        List<? extends Element> members = elementUtils.getAllMembers(typeElement);
        for (Element e : members) {
            if (e.getKind() == ElementKind.METHOD) {
                Action action = actionExtractor.findAction((ExecutableElement) e);
                if (action != null) {
                    resource.addAction(action);
                }
            }
        }

        if (!resource.getActions().isEmpty()) {
            warehouse.addResource(resource);
        }
    }

    /**
     * 其实就是把 XxController 的 Controller 剪掉了
     */
    private String findResourceName(TypeElement typeElement) {
        Name simpleName = typeElement.getSimpleName();
        return simpleName.subSequence(0, simpleName.length() - 10).toString();
    }

    private String getControllerPath(TypeElement element) {
        RequestMapping requestMapping = element.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return findRequestMapping(requestMapping.value(), requestMapping.path());
        }
        return "";
    }
}
