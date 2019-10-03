package com.chendayu.c2d.processor.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.chendayu.c2d.processor.SupportedContentType;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.property.Comment;
import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.util.NameConversions;
import com.chendayu.c2d.processor.util.StringBuilderHolder;

import org.springframework.http.HttpMethod;

/**
 * 对资源的一个操作，相当于一个 api 接口，以及一个方法
 */
public class Action {

    /**
     * 操作名字
     */
    private final String name;

    /**
     * 包含 {@link Resource} 名字的全名
     */
    private final String fullName;

    /**
     * 链接，用于生成文档
     */
    private final String link;

    /**
     * 完整路径
     */
    private final String path;

    /**
     * 请求方法
     */
    private final HttpMethod method;

    /**
     * 方法上的注释
     */
    private final Comment comment;

    /**
     * 操作描述
     */
    private String description;

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
    private SupportedContentType responseContentType;

    public Action(Resource resource, String name, String path, HttpMethod method, Comment comment) {
        this.name = name;
        String resourceName = resource.getName();
        this.fullName = NameConversions.actionFullName(resourceName, name);

        String basePath = resource.getPath();
        this.path = StringBuilderHolder.resetAndGet().append(basePath).append(path).toString();

        this.method = method;
        this.link = NameConversions.actionLink(resource.getLink(), name);

        this.comment = comment;
        this.description = comment.getCommentText();
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

    public String getFullName() {
        return fullName;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
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

    public SupportedContentType getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(SupportedContentType responseContentType) {
        this.responseContentType = responseContentType;
    }

    /**
     * 查找参数的注释
     *
     * @param name 参数名
     * @return 参数的注释
     */
    public String findParameterDescription(String name) {
        return comment.getParamComment(name);
    }

    public boolean hasRequestBody() {
        if (requestBody == null) {
            return false;
        }

        DeclarationType type = requestBody.getType();
        return type != DeclarationType.VOID && type != DeclarationType.UNKNOWN;
    }

    public boolean hasResponseBody() {
        return responseBody != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(link, action.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
