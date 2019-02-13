package com.chendayu.dydoc.processor;

import java.util.List;

public class DocGenerator {

    private StringBuilder builder = new StringBuilder(2048);

    public String generate(Resource resource) {

        insertSafeLine();
        writeResourceInfo(resource);
        return generate();
    }

    private void insertSafeLine() {
        appendLine("\n//插入本行以避免文件include到一起时可能出现的格式错乱");
    }

    private void writeResourceInfo(Resource resource) {
        title2(resource.getName());

        if (!resource.getDescription().isEmpty()) {
            appendLine(resource.getDescription());
        }

        for (Action action : resource.getActions()) {
            title3(action.getName());
            if (!action.getDescription().isEmpty()) {
                appendLine(action.getDescription());
            }
            title4("Request");

            builder.append("[source,http]\n")
                    .append("----\n")
                    .append(action.getMethod()).append(' ')
                    .append(resource.getPath())
                    .append(action.getPath()).append('\n')
                    .append("----");
            separator();

            if (!action.getUrlParameters().isEmpty()) {
                title5("Path parameters");
                parameterTable(action.getUrlParameters());
            }

            if (!action.getPathVariables().isEmpty()) {
                title5("URL parameters");
                parameterTable(action.getPathVariables());
            }

            if (!action.getBodyFields().isEmpty()) {
                title5("Body Fields");
                parameterTable(action.getBodyFields());
            }
        }
    }


    private void appendLine(String line) {
        builder.append(line);
        separator();
    }

    private void title2(String title) {
        builder.append("== ").append(title);
        separator();
    }

    private void title3(String title) {
        builder.append("=== ").append(title);
        separator();
    }

    private void title4(String title) {
        builder.append("==== ").append(title);
        separator();
    }

    private void title5(String title) {
        builder.append("===== ").append(title);
        separator();
    }

    private void separator() {
        this.builder.append('\n').append('\n');
    }


    private String generate() {
        String result = builder.toString();
        builder.setLength(0);
        return result;
    }

    private void parameterTable(List<Parameter> parameters) {

        builder.append("|===\n");

        builder.append("| Name | Type | Description\n");

        for (Parameter parameter : parameters) {
            builder.append("| ").append(parameter.getName())
                    .append(" | ").append(parameter.getType())
                    .append(" | ").append(parameter.getDescription())
                    .append('\n');
        }

        builder.append("|===\n\n");
    }
}
