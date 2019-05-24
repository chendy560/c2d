package com.chendayu.c2d.processor.resource;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;

import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.action.ActionExtractor;

import org.springframework.web.bind.annotation.RequestMapping;

import static com.chendayu.c2d.processor.Utils.findRequestMapping;

public class ResourceExtractor extends InfoExtractor {

    private final ActionExtractor actionExtractor;

    public ResourceExtractor(ProcessingEnvironment processingEnv, Warehouse warehouse) {
        super(processingEnv, warehouse);
        this.actionExtractor = new ActionExtractor(processingEnv, warehouse);
    }

    public void extract(TypeElement typeElement) {
        String resourceName = findResourceName(typeElement);
        if (warehouse.containsResource(resourceName)) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "resource '" + resourceName + " already exists");
            return;
        }

        Resource resource = new Resource(resourceName);
        StringBuilder requestMappingBuilder = new StringBuilder();
        getControllerPath(typeElement, requestMappingBuilder);
        String path = requestMappingBuilder.toString();
        resource.setPath(path);

        List<? extends Element> members = elementUtils.getAllMembers(typeElement);
        for (Element e : members) {
            if (e.getKind() == ElementKind.METHOD) {
                Action action = actionExtractor.findAction((ExecutableElement) e);
                if (action != null) {
                    action.setBasePath(path);
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
     * 换句话说如果不按约定命名的话，这里不知道能拿到什么玩意…
     */
    private String findResourceName(TypeElement typeElement) {
        Name simpleName = typeElement.getSimpleName();
        return simpleName.subSequence(0, simpleName.length() - 10).toString();
    }

    private void getControllerPath(TypeElement element, StringBuilder builder) {
        TypeMirror superclass = element.getSuperclass();
        if (superclass.getKind() != TypeKind.NONE) {
            getControllerPath((TypeElement) typeUtils.asElement(superclass), builder);
        }

        RequestMapping requestMapping = element.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            builder.append(findRequestMapping(requestMapping.value(), requestMapping.path()));
        }
    }
}
