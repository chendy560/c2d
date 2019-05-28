package com.chendayu.c2d.processor.action;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.chendayu.c2d.processor.AbstractComponent;
import com.chendayu.c2d.processor.DocIgnore;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 忽略掉应该忽略的参数的参数处理器
 */
public class IgnoreParameterHandler extends AbstractComponent implements ParameterHandler {

    /**
     * 一些会被spring特殊处理的类型，我们就不去处理了
     * RedirectAttributes 和 BindingResult 有接口在里面了，所以没有包含进来
     */
    private static final List<String> ignoreTypeNames = Arrays.asList(
            "org.springframework.web.context.request.RequestAttributes",
            "javax.servlet.ServletRequest",
            "javax.servlet.ServletResponse",
            "javax.servlet.http.HttpSession",
            "java.security.Principal",
            "org.springframework.http.HttpMethod",
            "java.util.Locale",
            "java.util.TimeZone",
            "java.util.ZoneId",
            "java.io.InputStream",
            "java.io.Reader",
            "java.io.OutputStream",
            "java.io.Writer",
            "org.springframework.http.HttpEntity",
            "org.springframework.ui.Model",
            "org.springframework.ui.ModelMap",
            "org.springframework.validation.Errors",
            "org.springframework.web.util.UriBuilder"
    );

    /**
     * 一些并不在请求中，或者我们认为不会去管的字段的注解
     */
    private static final List<Class<? extends Annotation>> ignoreAnnotations = Arrays.asList(
            SessionAttribute.class,
            RequestAttribute.class,
            CookieValue.class,
            RequestHeader.class,
            MatrixVariable.class,
            DocIgnore.class
    );

    private final List<TypeMirror> ignoreParameterTypes;

    public IgnoreParameterHandler(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
        ArrayList<TypeMirror> types = new ArrayList<>(ignoreTypeNames.size());
        for (String ignoreTypeName : ignoreTypeNames) {
            TypeElement element = elementUtils.getTypeElement(ignoreTypeName);
            if (element == null) {
                continue;
            }
            types.add(element.asType());
        }
        this.ignoreParameterTypes = Collections.unmodifiableList(types);
    }

    @Override
    public boolean handleParameter(Action action, VariableElement element) {
        TypeMirror type = element.asType();

        for (TypeMirror ignoreParameterType : ignoreParameterTypes) {
            if (typeUtils.isAssignable(type, ignoreParameterType)) {
                return true;
            }
        }

        for (Class<? extends Annotation> ignoreAnnotation : ignoreAnnotations) {
            if (element.getAnnotation(ignoreAnnotation) != null) {
                return true;
            }
        }

        return false;
    }
}
