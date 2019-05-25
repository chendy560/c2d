package com.chendayu.c2d.processor.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chendayu.c2d.processor.SupportedContentType;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.model.DocComment;
import com.chendayu.c2d.processor.property.Property;

import org.springframework.http.HttpMethod;

/**
 * 对资源的一个操作，相当于一个 api 接口，以及一个方法
 */
public class Action {

    /**
     * 操作名字
     */
    private String name;

    /**
     * 操作描述
     */
    private List<String> description;

    /**
     * 方法上的注释
     */
    private DocComment docComment;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 路径参数
     */
    private List<Property> pathVariables = Collections.emptyList();

    /**
     * url参数
     */
    private List<Property> urlParameters = Collections.emptyList();

    /**
     * 请求body，在请求没有body时为null
     */
    private Property requestBody;

    /**
     * 可以处理的请求的内容类型
     */
    private SupportedContentType requestContentType;

    /**
     * 响应body，在响应没有body时为null
     */
    private Property responseBody;

    /**
     * 响应body的内容类型
     */
    private SupportedContentType responseBodyContentType;

    public Action(String name, DocComment docComment) {
        this.name = name;
        this.docComment = docComment;
        this.description = docComment.getDescription();
    }

    public void addPathVariable(Property property) {
        if (pathVariables.isEmpty()) {
            pathVariables = new ArrayList<>();
        }
        pathVariables.add(property);
    }

    public void addUrlParameter(Property property) {
        if (urlParameters.isEmpty()) {
            urlParameters = new ArrayList<>();
        }
        urlParameters.add(property);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public List<Property> getPathVariables() {
        return pathVariables;
    }

    public List<Property> getUrlParameters() {
        return urlParameters;
    }

    public Property getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Property requestBody) {
        this.requestBody = requestBody;
    }

    public Property getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Property responseBody) {
        this.responseBody = responseBody;
    }

    public SupportedContentType getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(SupportedContentType requestContentType) {
        this.requestContentType = requestContentType;
    }

    public SupportedContentType getResponseBodyContentType() {
        return responseBodyContentType;
    }

    public void setResponseBodyContentType(SupportedContentType responseBodyContentType) {
        this.responseBodyContentType = responseBodyContentType;
    }

    public void setBasePath(String s) {
        this.path = s + path;
    }

    /**
     * 查找参数的注释
     *
     * @param name 参数名
     * @return 参数的注释
     */
    public List<String> findParameterDescription(String name) {
        return docComment.getParam(name);
    }

    public boolean hasRequestBody() {
        if (requestBody == null) {
            return false;
        }

        DeclarationType type = requestBody.getType();
        return type != DeclarationType.VOID && type != DeclarationType.UNKNOWN;
    }

    public boolean hasResponseBody() {
        if (responseBody == null) {
            return false;
        }

        DeclarationType type = responseBody.getType();
        return type != DeclarationType.VOID && type != DeclarationType.UNKNOWN;
    }
}
