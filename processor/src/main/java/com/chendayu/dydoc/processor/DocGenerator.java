package com.chendayu.dydoc.processor;

import java.util.ArrayList;
import java.util.List;

public class DocGenerator {

    private AdocGenerator generator = new AdocGenerator();

    public String generateIndex(Index index) {
        generator.title0("Index")
                .dualNewLine()
                .title1("Resources")
                .dualNewLine();

        for (String resource : index.getResources()) {
            generator.include("resources/" + resource).newLine();
        }

        generator.title1("Objects")
                .dualNewLine();
        for (String object : index.getObjects()) {
            generator.include("objects/" + object).newLine();
        }
        return generator.getAndReset();
    }

    public String generateResourcePages(Resource resource) {

        generator.title2(resource.getName())
                .newLine();

        for (Action action : resource.getActions()) {
            generator.title3(action.getName()).dualNewLine();
            appendLines(action.getDescription());


            generator.title4("Request")
                    .dualNewLine()
                    .sourceCode("http")
                    .newLine()
                    .codeBoundary()
                    .newLine()
                    .append(action.getMethod().toString()).space().append(resource.getPath()).append(action.getPath())
                    .newLine().
                    codeBoundary().newLine();

            if (!action.getUrlParameters().isEmpty()) {
                generator.title5("Path Parameters").dualNewLine();
                parameterTable(action.getUrlParameters());
            }

            if (!action.getPathVariables().isEmpty()) {
                generator.title5("URL Parameters").dualNewLine();
                parameterTable(action.getPathVariables());
            }

            if (action.getRequestBody() != null) {
                generator.title5("Request Body").dualNewLine()
                        .newLine();
                objectLink(action.getRequestBody());
                generator.dualNewLine();
            }
            if (action.getResponseBody() != null) {
                generator.title5("Response").dualNewLine();
                objectLink(action.getResponseBody());
                generator.dualNewLine();
            }
        }
        return generator.getAndReset();
    }

    public String generateObjectPages(ObjectStruct objectStruct) {

        generator.anchor(objectStruct.getHash())
                .newLine()
                .title2(objectStruct.getName())
                .newLine();
        appendLines(objectStruct.getDescription());
        parameterTable(objectStruct.getParameters());
        return generator.getAndReset();
    }

    private void parameterTable(List<Parameter> parameters) {

        generator.tableBoundary().newLine()
                .tableSeparator().append("Name").space()
                .tableSeparator().append("Type").space()
                .tableSeparator().append("Description").dualNewLine();

        for (Parameter parameter : parameters) {
            generator.tableSeparator().append(parameter.getName()).newLine();
            ParameterType type = parameter.getType();
            if (type.isSimpleType()) {
                generator.tableSeparator().append(type.getName()).newLine();
            } else {
                generator.tableSeparator();
                objectLink(parameter);
                generator.newLine();
            }
            generator.tableSeparator();
            appendLines(parameter.getDescription());
        }

        generator.tableBoundary().dualNewLine();
    }

    private void objectLink(Parameter p) {
        generator.link(p.getObjectHash(), p.getObjectName())
                .append(p.getType().getName());
    }

    public void appendLines(List<String> ss) {
        if (ss.isEmpty()) {
            generator.newLine();
            return;
        }

        for (String s : ss) {
            if (!s.isEmpty()) {
                generator.append(s).hardNewLine();
            }
        }

        generator.shrink(3);
        generator.dualNewLine();
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
