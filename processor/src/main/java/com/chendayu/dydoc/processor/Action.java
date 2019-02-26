package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Action {

    private String name;

    private List<String> description;

    private String path;

    private HttpMethod method;

    private List<Property> pathVariables = Collections.emptyList();

    private List<Property> urlParameters = Collections.emptyList();

    private Property requestBody;

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

    public void setDescription(List<String> description) {
        this.description = description;
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

    public void setPathVariables(List<Property> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public List<Property> getUrlParameters() {
        return urlParameters;
    }

    public void setUrlParameters(List<Property> urlParameters) {
        this.urlParameters = urlParameters;
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
}
