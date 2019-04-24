package com.chendayu.c2d.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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

    /**
     * @return element 擦除之后的类型
     */
    protected final TypeMirror asErasedType(VariableElement element) {
        TypeMirror type = element.asType();
        return typeUtils.erasure(type);
    }

    /**
     * @return element 擦除之后的类型
     */
    protected final TypeMirror asErasedType(TypeElement element) {
        TypeMirror type = element.asType();
        return typeUtils.erasure(type);
    }
}
