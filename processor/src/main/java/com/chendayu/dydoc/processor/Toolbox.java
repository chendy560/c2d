package com.chendayu.dydoc.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * 注解处理的工具类太碎怎么办？
 * 整一起代理一波啊
 */
public class Toolbox implements Elements, Types, Messager {

    private final Elements elements;

    private final Types types;

    private final Messager messager;

    public Toolbox(ProcessingEnvironment env) {
        this.elements = env.getElementUtils();
        this.types = env.getTypeUtils();
        this.messager = env.getMessager();
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
        messager.printMessage(kind, msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {
        messager.printMessage(kind, msg, e);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        messager.printMessage(kind, msg, e, a);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        messager.printMessage(kind, msg, e, a, v);
    }

    @Override
    public Element asElement(TypeMirror t) {
        return types.asElement(t);
    }

    @Override
    public boolean isSameType(TypeMirror t1, TypeMirror t2) {
        return types.isSameType(t1, t2);
    }

    @Override
    public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
        return types.isSubtype(t1, t2);
    }

    @Override
    public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        return types.isAssignable(t1, t2);
    }

    @Override
    public boolean contains(TypeMirror t1, TypeMirror t2) {
        return types.contains(t1, t2);
    }

    @Override
    public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
        return types.isSubsignature(m1, m2);
    }

    @Override
    public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
        return types.directSupertypes(t);
    }

    @Override
    public TypeMirror erasure(TypeMirror t) {
        return types.erasure(t);
    }

    @Override
    public TypeElement boxedClass(PrimitiveType p) {
        return types.boxedClass(p);
    }

    @Override
    public PrimitiveType unboxedType(TypeMirror t) {
        return types.unboxedType(t);
    }

    @Override
    public TypeMirror capture(TypeMirror t) {
        return types.capture(t);
    }

    @Override
    public PrimitiveType getPrimitiveType(TypeKind kind) {
        return types.getPrimitiveType(kind);
    }

    @Override
    public NullType getNullType() {
        return types.getNullType();
    }

    @Override
    public NoType getNoType(TypeKind kind) {
        return types.getNoType(kind);
    }

    @Override
    public ArrayType getArrayType(TypeMirror componentType) {
        return types.getArrayType(componentType);
    }

    @Override
    public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        return types.getWildcardType(extendsBound, superBound);
    }

    @Override
    public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
        return types.getDeclaredType(typeElem, typeArgs);
    }

    @Override
    public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
        return types.getDeclaredType(containing, typeElem, typeArgs);
    }

    @Override
    public TypeMirror asMemberOf(DeclaredType containing, Element element) {
        return types.asMemberOf(containing, element);
    }

    @Override
    public PackageElement getPackageElement(CharSequence name) {
        return elements.getPackageElement(name);
    }

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        return elements.getTypeElement(name);
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
        return elements.getElementValuesWithDefaults(a);
    }

    @Override
    public String getDocComment(Element e) {
        return elements.getDocComment(e);
    }

    @Override
    public boolean isDeprecated(Element e) {
        return elements.isDeprecated(e);
    }

    @Override
    public Name getBinaryName(TypeElement type) {
        return elements.getBinaryName(type);
    }

    @Override
    public PackageElement getPackageOf(Element type) {
        return elements.getPackageOf(type);
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement type) {
        return elements.getAllMembers(type);
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        return elements.getAllAnnotationMirrors(e);
    }

    @Override
    public boolean hides(Element hider, Element hidden) {
        return elements.hides(hider, hidden);
    }

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        return elements.overrides(overrider, overridden, type);
    }

    @Override
    public String getConstantExpression(Object value) {
        return elements.getConstantExpression(value);
    }

    @Override
    public void printElements(Writer w, Element... elements) {
        this.elements.printElements(w, elements);
    }

    @Override
    public Name getName(CharSequence cs) {
        return elements.getName(cs);
    }

    @Override
    public boolean isFunctionalInterface(TypeElement type) {
        return elements.isFunctionalInterface(type);
    }
}
