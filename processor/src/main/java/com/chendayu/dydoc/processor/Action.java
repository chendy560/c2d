package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Action {

    private String name;

    private String description = "";

    private String path;

    private HttpMethod method;

    private List<Parameter> pathVariables = Collections.emptyList();

    private List<Parameter> urlParameters = Collections.emptyList();

    private List<Parameter> bodyFields = Collections.emptyList();

    public void addPathVariable(Parameter parameter) {
        if (pathVariables.isEmpty()) {
            pathVariables = new ArrayList<>();
        }
        pathVariables.add(parameter);
    }

    public void addUrlParameter(Parameter parameter) {
        if (urlParameters.isEmpty()) {
            urlParameters = new ArrayList<>();
        }
        urlParameters.add(parameter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public List<Parameter> getPathVariables() {
        return pathVariables;
    }

    public List<Parameter> getUrlParameters() {
        return urlParameters;
    }

    public List<Parameter> getBodyFields() {
        return bodyFields;
    }

    public void setBodyFields(List<Parameter> bodyFields) {
        this.bodyFields = bodyFields;
    }
}
