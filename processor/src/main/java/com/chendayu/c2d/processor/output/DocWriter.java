package com.chendayu.c2d.processor.output;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.declaration.TypeVarDeclaration;
import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.resource.Resource;

public class DocWriter {

    private final TreeMap<String, Declaration> declarationMap = new TreeMap<>();

    private final AdocWriter adoc;

    public DocWriter(Writer writer) {
        this.adoc = new AdocWriter(writer);
    }

    public void printDoc(Warehouse warehouse) {

        writeTitle(warehouse);

        writeResources(warehouse.getResources());

        writeDeclarations();
    }

    private void writeResources(Collection<Resource> resources) {
        adoc.title1("资源");

        for (Resource resource : resources) {
            adoc.title2(resource.getName());

            SortedSet<Action> actions = resource.getActions();
            for (Action action : actions) {
                writeActions(action);
            }
        }
    }

    private void writeActions(Action action) {
        String name = action.getName();
        adoc.title3(name);
        adoc.appendLines(action.getDescription());

        adoc.sourceCodeBegin("http")
                .append(action.getMethod().name()).space().append(action.getPath())
                .newLine()
                .sourceCodeEnd();

        List<Property> pathVariables = action.getPathVariables();
        if (!pathVariables.isEmpty()) {
            adoc.title4("路径参数");
            for (Property pathVariable : pathVariables) {
                saveDeclaration(pathVariable.getDeclaration());
            }
            parameterTable(pathVariables);
        }

        List<Property> urlParameters = action.getUrlParameters();
        if (!urlParameters.isEmpty()) {
            adoc.title4("URL参数");
            for (Property property : urlParameters) {
                saveDeclaration(property.getDeclaration());
            }
            parameterTable(urlParameters);
        }

        Property requestBody = action.getRequestBody();
        if (requestBody != null) {
            adoc.title4("请求Body");
            writeType(requestBody.getDeclaration());
            saveDeclaration(requestBody.getDeclaration());
            adoc.dualNewLine();
        }

        Property responseBody = action.getResponseBody();
        if (responseBody != null) {
            adoc.title4("响应");
            writeType(responseBody.getDeclaration());
            saveDeclaration(responseBody.getDeclaration());
            adoc.dualNewLine();
        }

        adoc.dualNewLine();
    }

    private void writeDeclarations() {
        adoc.title1("对象结构");

        for (Declaration declaration : declarationMap.values()) {
            DeclarationType type = declaration.getType();
            switch (type) {
                case OBJECT:
                    NestedDeclaration od = (NestedDeclaration) declaration;
                    adoc.anchor(od.getHash()).title3(od.getShortName());

                    adoc.appendLines(od.getDescription());
                    List<Property> typeParameters = od.getTypeParameters();
                    if (!typeParameters.isEmpty()) {
                        adoc.title4("类型参数");
                        parameterTable(typeParameters);
                    }

                    Collection<Property> properties = od.gettableProperties();
                    if (!properties.isEmpty()) {
                        adoc.title4("字段");
                        parameterTable(properties);
                    }
                    break;
                case ENUM:
                    EnumDeclaration ed = (EnumDeclaration) declaration;
                    adoc.anchor(ed.getHash()).title3(ed.getName());
                    List<Property> constants = ed.getConstants();
                    if (!constants.isEmpty()) {
                        adoc.title4("常量列表");
                        parameterTable(constants);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void writeTitle(Warehouse warehouse) {
        String applicationName = warehouse.getApplicationName();

        adoc.title0(applicationName + " API 文档");
    }

    private void saveDeclaration(Declaration declaration) {
        DeclarationType type = declaration.getType();
        if (type == DeclarationType.OBJECT) {
            NestedDeclaration od = (NestedDeclaration) declaration;

            if (declarationMap.containsKey(od.getShortName()) && od.getTypeArguments().isEmpty()) {
                return;
            }
            declarationMap.put(od.getShortName(), od);

            for (Declaration typeArg : od.getTypeArguments()) {
                saveDeclaration(typeArg);
            }

            for (Property property : od.gettableProperties()) {
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

            if (declarationMap.containsKey(ed.getName())) {
                return;
            }

            declarationMap.put(ed.getName(), ed);
        }
    }

    private void parameterTable(Collection<? extends Property> parameters) {

        adoc.col("3,7");
        adoc.tableBegin()
                .columnBegin().append("Name").space()
                .columnBegin().append("Description").dualNewLine();

        for (Property p : parameters) {
            adoc.columnBegin().appendBoldMonospace(p.getDisplayName()).append("  :  ");
            writeType(p.getDeclaration());
            adoc.newLine();
            adoc.columnBegin()
                    .appendLines(p.getDescription());
        }

        adoc.tableEnd();
    }

    private void writeType(Declaration d) {
        DeclarationType type = d.getType();
        switch (type) {
            case STRING:
                adoc.appendItalic("String");
                break;
            case NUMBER:
                adoc.appendItalic("Number");
                break;
            case TIMESTAMP:
                adoc.appendItalic("Timestamp");
                break;
            case BOOLEAN:
                adoc.appendItalic("Boolean");
                break;
            case ENUM_CONST:
                adoc.appendItalic("Enum");
                break;
            case DYNAMIC:
                adoc.appendItalic("Any");
                break;
            case TYPE_PARAMETER:
                TypeVarDeclaration tad = (TypeVarDeclaration) d;
                adoc.appendItalic("TypeParameter").appendItalic(tad.getName());
                break;
            case VOID:
                adoc.appendItalic("None");
                break;
            case FILE:
                adoc.appendItalic("File");
                break;
            case ENUM:
                EnumDeclaration ed = (EnumDeclaration) d;
                adoc.link(ed.getHash(), ed.getName());
                break;
            case ARRAY:
                ArrayDeclaration ad = (ArrayDeclaration) d;
                Declaration cd = ad.getItemType();
                adoc.appendItalic("Array<");
                writeType(cd);
                adoc.appendItalic('>');
                break;
            case OBJECT:
                NestedDeclaration od = (NestedDeclaration) d;
                List<Declaration> typeArgs = od.getTypeArguments();
                if (typeArgs.isEmpty()) {
                    adoc.link(od.getHash(), od.getShortName());
                } else {
                    adoc.link(od.getHash(), od.getShortName()).append('<');
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
