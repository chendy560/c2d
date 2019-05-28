package com.chendayu.c2d.processor.resource;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import com.chendayu.c2d.processor.DocIgnore;
import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.action.ActionExtractor;

import org.springframework.web.bind.annotation.RequestMapping;

import static com.chendayu.c2d.processor.Utils.findRequestMapping;

public class ResourceExtractor extends InfoExtractor {

    private static final String CONTROLLER = "Controller";

    private static final int CONTROLLER_LENGTH = CONTROLLER.length();

    private final ActionExtractor actionExtractor;

    public ResourceExtractor(ProcessingEnvironment processingEnv, Warehouse warehouse) {
        super(processingEnv, warehouse);
        this.actionExtractor = new ActionExtractor(processingEnv, warehouse);
    }

    public void extract(TypeElement typeElement) {

        // 不处理 DocIgnore 的类
        if (shouldIgnore(typeElement)) {
            return;
        }

        String resourceName = findResourceName(typeElement);
        if (warehouse.containsResource(resourceName)) {
            logWarn("resource '" + resourceName + " already exists");
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

    private boolean shouldIgnore(TypeElement typeElement) {
        DocIgnore docIgnore = typeElement.getAnnotation(DocIgnore.class);
        return docIgnore != null && docIgnore.value();
    }

    private String findResourceName(TypeElement typeElement) {
        String simpleNameString = typeElement.getSimpleName().toString();

        if (simpleNameString.endsWith(CONTROLLER)) {
            return simpleNameString.substring(0, simpleNameString.length() - CONTROLLER_LENGTH);
        }

        return simpleNameString;
    }

    /**
     * 获取 Controller 类上的 RequestMapping 中配置的路径
     * 由于父类上的 RequestMapping 会继承给子类，所以需要递归，所以这里的参数是一个 StringBuilder
     */
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
