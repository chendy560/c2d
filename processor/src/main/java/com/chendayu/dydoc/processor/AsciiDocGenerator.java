package com.chendayu.dydoc.processor;

import java.util.ArrayList;
import java.util.List;

public class AsciiDocGenerator {

    private StringBuilder builder = new StringBuilder(2048);

    public String generateIndex(Index index) {
        title1("index");
        title2("Resources");
        for (String resource : index.getResources()) {
            builder.append("include::").append("resources/").append(resource).append("[]\n");
        }
        title2("Objects");
        for (String object : index.getObjects()) {
            builder.append("include::").append("objects/").append(object).append("[]\n");
        }
        return generatePages();
    }

    public String generateResourcePages(Resource resource) {

        insertSafeLine();
        writeResourceInfo(resource);
        return generatePages();
    }

    public String generateObjectPages(ObjectStruct objectStruct) {
        insertSafeLine();
        insertAnchor(objectStruct.getHash());
        title3(objectStruct.getName());
        parameterTable(objectStruct.getParameters());
        return generatePages();
    }

    private void insertSafeLine() {
        appendLine("\n//插入本行以避免文件include到一起时可能出现的格式错乱\n");
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
            if (action.getRequestBody() != null) {
                title4("Request");
                printBody(action.getRequestBody());
            }
            if (action.getResponseBody() != null) {
                title4("Response");
                printBody(action.getResponseBody());
            }
        }
    }

    private void appendLine(String line) {
        builder.append(line).append(" +\n");
    }

    private void title1(String title) {
        builder.append("= ").append(title);
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


    private String generatePages() {
        String result = builder.toString();
        builder.setLength(0);
        return result;
    }

    private void printBody(Parameter parameter) {
        builder.append(typeString(parameter))
                .append("+\n");
        appendDescription(parameter.getDescription());
        builder.append('\n');
        builder.append('\n');
    }

    private void parameterTable(List<Parameter> parameters) {

        builder.append("|===\n");

        builder.append("| Name | Type | Description\n");

        for (Parameter parameter : parameters) {
            builder.append("\n| ").append(parameter.getName())
                    .append("\n| ").append(typeString(parameter))
                    .append("\n| ");

            appendDescription(parameter.getDescription());

            builder.append('\n');
            builder.append('\n');
        }

        builder.append("|===\n\n");
    }

    private void appendDescription(List<String> description) {
        if (!description.isEmpty()) {
            for (String s : description) {
                builder.append(s).append(" +\n");
            }
            builder.setLength(builder.length() - 3);
        }
    }

    private String typeString(Parameter parameter) {
        if (parameter.getType() == ParameterType.OBJECT) {
            return link(parameter.getObjectHash(), parameter.getObjectName() + "对象");
        }
        return parameter.getType().name();
    }

    private void insertAnchor(String anchor) {
        builder.append("\n[[").append(anchor).append("]]\n");
    }

    private String link(String anchor, String name) {
        return "<<" + anchor + "," + name + ">>";
    }

    public static class Index {

        private List<String> resources = new ArrayList<>();

        private List<String> objects = new ArrayList<>();

        public void addResourceFile(String f) {
            resources.add(f);
        }

        public void addObjectFile(String f) {
            objects.add(f);
        }

        public List<String> getResources() {
            return resources;
        }

        public List<String> getObjects() {
            return objects;
        }
    }
}
