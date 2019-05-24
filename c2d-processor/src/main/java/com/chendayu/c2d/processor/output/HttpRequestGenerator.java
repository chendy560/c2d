package com.chendayu.c2d.processor.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.property.Property;

import org.springframework.http.HttpMethod;

public class HttpRequestGenerator {

    private static final int INDENT_SIZE = 2;

    private static final int NO_INDEX = 0;

    private static final String HTTP1 = "HTTP/1.1";
    private static final char QUERY_STRING_BEGIN = '?';
    private static final char PARAMETER_SPLIT = '&';
    private static final char PARAMETER_BEGIN = '{';
    private static final char PARAMETER_END = '}';
    private static final char EQUAL = '=';

    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_ALL = "*/*";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";

    private static final String HEADER_SPLIT = ": ";

    private static final String HTTP_OK = "200 OK";

    private final StringBuilder builder = new StringBuilder(16 * 1024);

    private final Map<String, Integer> writeDeclaration = new HashMap<>();

    public String generate(Action action) {
        generateRequestLine(action);
        writeRequest(action);
        writeResponse(action);
        return generateAndCleanup();
    }

    private void generateRequestLine(Action action) {
        writeHttpMethod(action);
        writePath(action);
        writeQueryString(action);
        writeHttpVersionAndFinish();
    }

    private void writeHttpMethod(Action action) {
        HttpMethod method = action.getMethod();
        builder.append(method.name()).append(' ');
    }

    private void writePath(Action action) {
        String path = action.getPath();
        builder.append(path);
    }

    private void writeQueryString(Action action) {
        List<Property> urlParameters = action.getUrlParameters();

        if (!urlParameters.isEmpty()) {
            builder.append(QUERY_STRING_BEGIN);

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

    private void writeHttpVersionAndFinish() {
        builder.append(' ').append(HTTP1).append('\n');
    }

    private void writeRequest(Action action) {
        boolean hasRequestBody = action.hasRequestBody();
        boolean hasResponseBody = action.hasResponseBody();

        if (hasRequestBody) {
            writeHeader(CONTENT_TYPE, APPLICATION_JSON_UTF_8);
        }

        if (hasResponseBody) {
            writeHeader(ACCEPT, APPLICATION_JSON_UTF_8);
        } else {
            writeHeader(ACCEPT, ACCEPT_ALL);
        }

        if (hasRequestBody) {
            builder.append('\n');
            writeBody(action.getRequestBody().getDeclaration());
            builder.append('\n');
        }
    }

    private void writeHeader(String name, String value) {
        builder.append(name).append(HEADER_SPLIT).append(value).append('\n');
    }

    private String generateAndCleanup() {
        return generateAndCleanup(builder);
    }

    public void writeBody(Declaration declaration) {
        generateJson(declaration, NO_INDEX);
    }

    private void writeResponse(Action action) {
        builder.append('\n');
        writeResponseLine();
        writeResponseHeaderAndBody(action);
    }

    private void writeResponseLine() {
        builder.append(HTTP1).append(' ').append(HTTP_OK).append('\n');
    }

    private void writeResponseHeaderAndBody(Action action) {
        boolean hasResponseBody = action.hasResponseBody();
        if (hasResponseBody) {

            writeHeader(CONTENT_TYPE, APPLICATION_JSON_UTF_8);

            builder.append('\n');

            writeBody(action.getResponseBody().getDeclaration());

            builder.append('\n');
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

                builder.append('\n');

                appendIndent(indent);
                builder.append(']');
                break;
            case OBJECT:
                builder.append('{').append('\n');

                NestedDeclaration nestedDeclaration = (NestedDeclaration) declaration;
                String qualifiedName = nestedDeclaration.getQualifiedName();
                int times = writeDeclaration.getOrDefault(qualifiedName, 0);

                if (times > 2) {
                    builder.append("null");
                } else {

                    writeDeclaration.put(qualifiedName, times + 1);
                    for (Property property : nestedDeclaration.gettableProperties()) {
                        generateJson(property, indent + 1);
                    }

                    cut(2); // 去掉结尾多出的一个 逗号 和一个 换行
                    builder.append('\n'); // 再补上换行
                    appendIndent(indent);
                    builder.append('}');
                    writeDeclaration.put(qualifiedName, times - 1);
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

    private String generateAndCleanup(StringBuilder builder) {
        String result = builder.toString();
        builder.setLength(0);
        return result;
    }

    private void cut(int length) {
        builder.setLength(builder.length() - length);
    }
}
