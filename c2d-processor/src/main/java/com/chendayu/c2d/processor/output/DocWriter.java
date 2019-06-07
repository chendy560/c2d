package com.chendayu.c2d.processor.output;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.action.Resource;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.declaration.TypeVarDeclaration;
import com.chendayu.c2d.processor.property.Property;

public class DocWriter {

    private final TreeMap<String, NestedDeclaration> nestedDeclarationMap = new TreeMap<>();
    private final TreeMap<String, EnumDeclaration> enumDeclarationMap = new TreeMap<>();

    private final AdocWriter adoc;

    private final HttpRequestGenerator httpRequestGenerator;

    public DocWriter(Writer writer) {
        this.adoc = new AdocWriter(writer);
        this.httpRequestGenerator = new HttpRequestGenerator();
    }

    public void printDoc(Warehouse warehouse) {

        writeTitle(warehouse);

        writeResources(warehouse.getResources());

        writeDeclarations();
    }

    private void writeResources(Collection<Resource> resources) {
        adoc.title1("Resources");

        for (Resource resource : resources) {
            adoc.title2(resource.getName());

            Collection<Action> actions = resource.getActions();
            for (Action action : actions) {
                writeActions(action);
            }
        }
    }

    private void writeActions(Action action) {
        String name = action.getName();
        adoc.title3(name);
        adoc.appendLines(action.getDescription());

        adoc.sourceCodeBegin("http");

        String request = httpRequestGenerator.generate(action);
        adoc.append(request);
        adoc.sourceCodeEnd();

        List<Property> pathVariables = action.getPathVariables();
        if (!pathVariables.isEmpty()) {
            adoc.title4("Path Variables");
            for (Property pathVariable : pathVariables) {
                saveDeclaration(pathVariable.getDeclaration());
            }
            parameterTable(pathVariables);
        }

        List<Property> urlParameters = action.getUrlParameters();
        if (!urlParameters.isEmpty()) {
            adoc.title4("URL Parameters");
            for (Property property : urlParameters) {
                saveDeclaration(property.getDeclaration());
            }
            parameterTable(urlParameters);
        }

        Property requestBody = action.getRequestBody();
        if (requestBody != null) {
            adoc.title4("Request Body");
            writeType(requestBody.getDeclaration());
            saveDeclaration(requestBody.getDeclaration());
        }

        adoc.dualNewLine();

        Property responseBody = action.getResponseBody();
        if (responseBody != null) {
            adoc.title4("Response Body");
            writeType(responseBody.getDeclaration());
            saveDeclaration(responseBody.getDeclaration());
        }

        adoc.dualNewLine();
    }

    private void writeDeclarations() {
        adoc.title1("Components");

        adoc.title2("Objects");

        for (NestedDeclaration od : nestedDeclarationMap.values()) {
            adoc.anchor(od.getLink());
            adoc.title3(od.getShortName());

            adoc.appendLines(od.getDescription());
            List<TypeVarDeclaration> typeParameters = od.getTypeParameters();
            if (!typeParameters.isEmpty()) {
                adoc.title4("Type Parameters");
                parameterTable(typeParameters);
            }

            Collection<Property> properties = od.accessibleProperties();
            if (!properties.isEmpty()) {
                adoc.title4("Fields");
                parameterTable(properties);
            }
        }

        adoc.title2("Enums");
        for (EnumDeclaration ed : enumDeclarationMap.values()) {
            adoc.anchor(ed.getLink());
            adoc.title3(ed.getName());
            List<Property> constants = ed.getConstants();
            if (!constants.isEmpty()) {
                adoc.title4("Const");
                parameterTable(constants);
            }
        }
    }

    private void writeTitle(Warehouse warehouse) {
        String applicationName = warehouse.getApplicationName();

        adoc.title0(applicationName + " API Doc");
    }

    private void saveDeclaration(Declaration declaration) {
        DeclarationType type = declaration.getType();
        if (type == DeclarationType.OBJECT) {
            NestedDeclaration od = (NestedDeclaration) declaration;

            if (nestedDeclarationMap.containsKey(od.getShortName()) && od.getTypeArguments().isEmpty()) {
                return;
            }
            nestedDeclarationMap.put(od.getShortName(), od);

            for (Declaration typeArg : od.getTypeArguments()) {
                saveDeclaration(typeArg);
            }

            for (Property property : od.accessibleProperties()) {
                saveDeclaration(property.getDeclaration());
            }

            return;
        }

        if (type == DeclarationType.ARRAY) {

            ArrayDeclaration ad = (ArrayDeclaration) declaration;
            Declaration componentType = ad.getItemType();
            saveDeclaration(componentType);
            return;
        }

        if (type == DeclarationType.ENUM) {
            EnumDeclaration ed = (EnumDeclaration) declaration;

            if (enumDeclarationMap.containsKey(ed.getName())) {
                return;
            }

            enumDeclarationMap.put(ed.getName(), ed);
        }
    }

    private void parameterTable(List<TypeVarDeclaration> parameters) {

        adoc.col("3,7");
        adoc.tableBoundary();
        adoc.columnBegin();
        adoc.append("Name");
        adoc.appendSpace();
        adoc.columnBegin();
        adoc.append("Description");
        adoc.dualNewLine();

        for (TypeVarDeclaration p : parameters) {
            adoc.columnBegin();
            adoc.appendBoldMonospace(p.getName());
            adoc.append(" : [small]#");
            writeType(p);
            adoc.append('#');
            adoc.newLine();
            adoc.columnBegin();
            adoc.appendLines(p.getDescription());
        }

        adoc.tableBoundary();
    }

    private void parameterTable(Collection<? extends Property> parameters) {

        adoc.col("3,7");
        adoc.tableBoundary();
        adoc.columnBegin();
        adoc.append("Name");
        adoc.appendSpace();
        adoc.columnBegin();
        adoc.append("Description");
        adoc.dualNewLine();

        for (Property p : parameters) {
            adoc.columnBegin();
            adoc.appendBoldMonospace(p.getDisplayName());
            adoc.append(" : [small]#");
            writeType(p.getDeclaration());
            adoc.append('#');
            adoc.newLine();
            adoc.columnBegin();
            adoc.appendLines(p.getDescription());
        }

        adoc.tableBoundary();
    }

    private void writeType(Declaration d) {
        DeclarationType type = d.getType();
        switch (type) {
            case STRING:
                adoc.append("string");
                break;
            case NUMBER:
                adoc.append("number");
                break;
            case TIMESTAMP:
                adoc.append("timestamp");
                break;
            case BOOLEAN:
                adoc.append("boolean");
                break;
            case ENUM_CONST:
                adoc.append("enum_const");
                break;
            case DYNAMIC:
                adoc.append("any");
                break;
            case TYPE_PARAMETER:
                adoc.append("type_parameter");
                break;
            case VOID:
                adoc.append("none");
                break;
            case FILE:
                adoc.append("file");
                break;
            case ENUM:
                EnumDeclaration ed = (EnumDeclaration) d;
                adoc.link(ed.getLink(), ed.getName());
                break;
            case ARRAY:
                ArrayDeclaration ad = (ArrayDeclaration) d;
                Declaration cd = ad.getItemType();
                adoc.append("array<");
                writeType(cd);
                adoc.append('>');
                break;
            case OBJECT:
                NestedDeclaration od = (NestedDeclaration) d;
                List<Declaration> typeArgs = od.getTypeArguments();
                if (typeArgs.isEmpty()) {
                    adoc.link(od.getLink(), od.getShortName());
                } else {
                    adoc.link(od.getLink(), od.getShortName());
                    adoc.append('<');
                    for (int i = 0; i < typeArgs.size(); i++) {
                        writeType(typeArgs.get(i));
                        if (i != typeArgs.size() - 1) {
                            adoc.append(" ,");
                        }
                    }
                    adoc.append('>');
                }

                break;
            case UNKNOWN:
                adoc.append("Unknown");
                break;
        }
    }
}
