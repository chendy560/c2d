package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Action {

    private final String name;

    private final List<String> description;

    private final String hash;

    private String path;

    private HttpMethod method;

    private List<Parameter> pathVariables = Collections.emptyList();

    private List<Parameter> urlParameters = Collections.emptyList();

    private Parameter requestBody;

    private Parameter returnBody;

    public Action(String name, List<String> description) {
        this.name = name;
        this.description = description;
        this.hash = Sha256.shortHash(name);
    }

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

    public String getHash() {
        return hash;
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

    public Parameter getRequestBody() {
        return requestBody;
    }

    public Parameter getResponseBody() {
        return returnBody;
    }

    public void setResponseBody(Parameter requestBody) {
        this.requestBody = requestBody;
    }

    public void setReturnBody(Parameter returnBody) {
        this.returnBody = returnBody;
    }
}
