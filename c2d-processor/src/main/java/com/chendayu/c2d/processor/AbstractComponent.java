package com.chendayu.c2d.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Arrays;

/**
 * 单纯为了让代码好写一点的通用父类，包含一些常用的字段和方法
 */
public abstract class AbstractComponent {

    protected final ProcessingEnvironment processingEnv;

    protected final Elements elementUtils;

    protected final Types typeUtils;

    protected final Messager messager;

    public AbstractComponent(ProcessingEnvironment processingEnv) {

        this.processingEnv = processingEnv;
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.messager = processingEnv.getMessager();
    }

    protected final void logWarn(String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message);
    }

    protected final void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    protected final void note(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    /**
     * 从 spring-boot-configuration-processor 抄来的方法，用来获取指定类型的 TypeMirror
     */
    protected TypeMirror getDeclaredType(Class<?> typeClass, int numberOfTypeArgs) {
        TypeMirror[] typeArgs = new TypeMirror[numberOfTypeArgs];
        Arrays.setAll(typeArgs, i -> typeUtils.getWildcardType(null, null));
        TypeElement typeElement = elementUtils.getTypeElement(typeClass.getName());
        try {
            return typeUtils.getDeclaredType(typeElement, typeArgs);
        } catch (IllegalArgumentException ex) {
            // Try again without generics for older Java versions
            return typeUtils.getDeclaredType(typeElement);
        }
    }
}
