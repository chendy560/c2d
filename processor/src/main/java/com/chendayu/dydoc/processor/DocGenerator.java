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

            action.getDescription().forEach(this::appendLine);
            separator();

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

            title4("Response");
        }
    }


    private void appendLine(String line) {
        builder.append(line).append(" +\n");
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
            builder.append("\n| ").append(parameter.getName())
                    .append("\n| ").append(parameter.getType())
                    .append("\n| ");

            List<String> description = parameter.getDescription();
            if (!description.isEmpty()) {
                for (String s : description) {
                    builder.append(s).append(" +\n");
                }
                builder.setLength(builder.length() - 3);
            }

            builder.append('\n');
            builder.append('\n');
        }

        builder.append("|===\n\n");
    }
}
