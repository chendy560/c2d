package com.chendayu.dydoc.processor;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chendayu.dydoc.processor.Utils.upperCaseFirst;

public class Action extends DocElement {

    private static final String PREFIX = "a";

    private String path;

    private HttpMethod method;

    private List<Parameter> pathVariables = Collections.emptyList();

    private List<Parameter> urlParameters = Collections.emptyList();

    private Parameter requestBody;

    private Parameter responseBody;

    public Action(String name, List<String> description) {
        super(upperCaseFirst(name));
        setDescription(description);
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

    @Override
    protected String getPrefix() {
        return PREFIX;
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

    public Parameter getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Parameter requestBody) {
        this.requestBody = requestBody;
    }

    public Parameter getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Parameter responseBody) {
        this.responseBody = responseBody;
    }
}
