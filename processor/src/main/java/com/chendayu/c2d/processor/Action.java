package com.chendayu.c2d.processor;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对资源的一个操作，相当于一个 api 接口
 */
public class Action {

    /**
     * 操作名字
     */
    private String name;

    private List<String> description;

    /**
     * 请求路径
     */
    private String path;

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
     * 响应body，在响应没有body时为null
     */
    private Property responseBody;

    public Action(String name, List<String> description) {
        this.name = name;
        this.description = description;
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

    public void setBasePath(String s) {
        this.path = s + path;
    }
}
