package com.chendayu.c2d.processor.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chendayu.c2d.processor.SupportedContentType;
import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.util.StringBuilderHolder;

import org.springframework.http.HttpMethod;

public class HttpDataGenerator {

    private static final int INDENT_SIZE = 2;

    private static final int NO_INDENT = 0;

    private static final char QUERY_STRING_BEGIN = '?';
    private static final char PARAMETER_SPLIT = '&';
    private static final char PARAMETER_BEGIN = '{';
    private static final char PARAMETER_END = '}';
    private static final char EQUAL = '=';

    private static final String FORM_BOUNDARY = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
    private static final String CONTENT_TYPE_MULTI_PART_WITH_BOUNDARY = "multipart/form-data; boundary=" + FORM_BOUNDARY;

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String HEADER_SPLIT = ": ";

    private static final String HTTP_OK = "200 OK";

    private final StringBuilder builder = StringBuilderHolder.resetAndGet();

    private final Map<String, Integer> writtenDeclaration = new HashMap<>();

    public String generateRequest(Action action) {

        boolean parameterInUrl = parameterInUrl(action);

        generateHttpMethod(action);
        generatePath(action);
        if (parameterInUrl && !action.getUrlParameters().isEmpty()) {
            builder.append(QUERY_STRING_BEGIN);
            generateQueryString(action);
        }
        newLine();

        generateRequestBody(action);
        return generateAndCleanup();
    }

    public String generateResponse(Action action) {
        generateResponseLine();
        generateResponseBody(action);
        return generateAndCleanup();
    }

    private boolean parameterInUrl(Action action) {
        SupportedContentType contentType = action.getRequestContentType();
        return contentType == null || !contentType.isParameterInBody();
    }

    private void generateHttpMethod(Action action) {
        HttpMethod method = action.getMethod();
        builder.append(method.name()).append(' ');
    }

    private void generatePath(Action action) {
        String path = action.getPath();
        builder.append(path);
    }

    private void generateQueryString(Action action) {
        List<Property> urlParameters = action.getUrlParameters();

        if (!urlParameters.isEmpty()) {
            for (Property urlParameter : urlParameters) {
                String name = urlParameter.getDisplayName();
                builder.append(name)
                        .append(EQUAL)
                        .append(PARAMETER_BEGIN).append(name).append(PARAMETER_END)
                        .append(PARAMETER_SPLIT);
            }

            cut(1); // 删掉最后多出来的 &
        }

    }

    private void generateRequestBody(Action action) {
        SupportedContentType contentType = action.getRequestContentType();
        if (contentType != null) {
            switch (contentType) {
                case APPLICATION_JSON:
                    generateContentType(SupportedContentType.APPLICATION_JSON.getValue());
                    newLine();
                    generateJson(action.getRequestBody().getDeclaration(), NO_INDENT);
                    newLine();
                    break;
                case MULTIPART_FORM_DATA:
                    generateContentType(CONTENT_TYPE_MULTI_PART_WITH_BOUNDARY);
                    newLine();
                    generateMultipart(action.getUrlParameters());
                    newLine();
                    break;
                case APPLICATION_FORM_URLENCODED:
                    generateContentType(SupportedContentType.APPLICATION_FORM_URLENCODED.getValue());
                    newLine();
                    generateQueryString(action);
                    newLine();
                    break;
                default:
                    break;
            }

            newLine();
        }
    }

    private void generateMultipart(List<Property> parameters) {
        for (Property parameter : parameters) {
            generateMultipart(parameter);
        }
    }

    private void generateMultipart(Property property) {
        newLine();
        builder.append("Content-Disposition: form-data; name=\"")
                .append(property.getDisplayName())
                .append('"');
        if (property.getType() == DeclarationType.FILE) {
            builder.append("; filename=\"")
                    .append(property.getDisplayName())
                    .append('"');
            newLine();
            newLine();
            builder.append(FORM_BOUNDARY);
        } else {
            newLine();
            newLine();
            builder.append(property.getDisplayName());
            newLine();
            builder.append(FORM_BOUNDARY);
        }
    }

    private void newLine() {
        builder.append('\n');
    }

    private void generateContentType(String value) {
        builder.append(HttpDataGenerator.CONTENT_TYPE).append(HEADER_SPLIT).append(value).append('\n');
    }

    private String generateAndCleanup() {
        String result = builder.toString();
        builder.setLength(0);
        writtenDeclaration.clear();
        return result;
    }

    private void generateResponseLine() {
        builder.append(HTTP_OK).append('\n');
    }

    private void generateResponseBody(Action action) {
        if (action.hasResponseBody()) {

            SupportedContentType contentType = action.getResponseBodyContentType();
            generateContentType(contentType.getValue());

            newLine();

            generateJson(action.getResponseBody().getDeclaration(), NO_INDENT);

            newLine();
        }
    }


    private void generateJson(Declaration declaration, int indent) {
        DeclarationType type = declaration.getType();
        switch (type) {
            case STRING:
                builder.append("\"\"");
                break;

            case NUMBER:
                builder.append("0");
                break;

            case TIMESTAMP:
                builder.append("1558592851356");
                break;

            case BOOLEAN:
                builder.append("true");
                break;

            case ENUM:
            case ENUM_CONST:
                EnumDeclaration enumDeclaration = (EnumDeclaration) declaration;
                List<Property> constants = enumDeclaration.getConstants();
                if (!constants.isEmpty()) {
                    builder.append('"').append(constants.get(0).getDisplayName()).append('"');
                } else {
                    builder.append('"').append(enumDeclaration.getName()).append('"');
                }
                break;

            case DYNAMIC:
                builder.append("{\"some-key\": \"some-value\"}");
                break;

            case ARRAY:
                builder.append('[').append('\n');
                appendIndent(indent + 1);

                ArrayDeclaration arrayDeclaration = (ArrayDeclaration) declaration;
                generateJson(arrayDeclaration.getItemType(), indent + 1);

                newLine();

                appendIndent(indent);
                builder.append(']');
                break;
            case OBJECT:

                NestedDeclaration nestedDeclaration = (NestedDeclaration) declaration;
                String qualifiedName = nestedDeclaration.getQualifiedName();
                int times = writtenDeclaration.getOrDefault(qualifiedName, 0);

                if (times > 1) {
                    builder.append("null");
                } else {
                    builder.append('{').append('\n');
                    writtenDeclaration.put(qualifiedName, times + 1);
                    for (Property property : nestedDeclaration.accessibleProperties()) {
                        generateJson(property, indent + 1);
                    }

                    cut(2); // 去掉结尾多出的一个 逗号 和一个 换行
                    newLine();
                    appendIndent(indent);
                    builder.append('}');
                }
                break;

            case TYPE_PARAMETER:
            case UNKNOWN:
            case VOID:
            case FILE:
            default:
                builder.append("\"我不应该出现在这里的…\"");
                break;
        }
    }

    private void generateJson(Property property, int indent) {
        String displayName = property.getDisplayName();
        appendIndent(indent);
        builder.append('"').append(displayName).append('"');
        builder.append(':').append(' ');
        generateJson(property.getDeclaration(), indent);
        builder.append(',').append('\n');
    }

    private void appendIndent(int indent) {
        for (int i = 0; i < indent * INDENT_SIZE; i++) {
            builder.append(' ');
        }
    }

    private void cut(int length) {
        builder.setLength(builder.length() - length);
    }
}
