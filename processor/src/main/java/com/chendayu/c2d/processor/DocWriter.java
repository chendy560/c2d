package com.chendayu.c2d.processor;

import java.io.Writer;
import java.util.*;

public class DocWriter {

    private final Writer writer;

    public DocWriter(Writer writer) {
        this.writer = writer;
    }

    public void printDoc(Warehouse warehouse) {

        TreeMap<String, Declaration> declarationMap = new TreeMap<>();

        Collection<Resource> resources = warehouse.getResources();
        String applicationName = warehouse.getApplicationName();
        AdocWriter adoc = new AdocWriter(writer);

        adoc.title0(applicationName + " API 文档")
                .title1("资源");

        for (Resource resource : resources) {
            adoc.title2(resource.getName());

            SortedSet<Action> actions = resource.getActions();
            for (Action action : actions) {
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
                        saveDeclaration(pathVariable.getDeclaration(), declarationMap);
                    }
                    parameterTable(pathVariables, adoc);
                }

                List<Property> urlParameters = action.getUrlParameters();
                if (!urlParameters.isEmpty()) {
                    adoc.title4("URL参数");
                    for (Property property : urlParameters) {
                        saveDeclaration(property.getDeclaration(), declarationMap);
                    }
                    parameterTable(urlParameters, adoc);
                }

                Property requestBody = action.getRequestBody();
                if (requestBody != null) {
                    adoc.title4("请求Body");
                    appendType(requestBody.getDeclaration(), adoc);
                    saveDeclaration(requestBody.getDeclaration(), declarationMap);
                    adoc.dualNewLine();
                }

                Property responseBody = action.getResponseBody();
                if (responseBody != null) {
                    adoc.title4("响应");
                    appendType(responseBody.getDeclaration(), adoc);
                    saveDeclaration(responseBody.getDeclaration(), declarationMap);
                    adoc.dualNewLine();
                }

                adoc.dualNewLine();
            }
        }

        adoc.title1("对象结构");

        for (Declaration declaration : declarationMap.values()) {
            DeclarationType type = declaration.getType();
            switch (type) {
                case OBJECT:
                    ObjectDeclaration od = (ObjectDeclaration) declaration;
                    adoc.anchor(od.getHash()).title3(od.getName());

                    adoc.appendLines(od.getDescription());
                    List<Property> typeParameters = od.getTypeParameters();
                    if (!typeParameters.isEmpty()) {
                        adoc.title4("类型参数");
                        parameterTable(typeParameters, adoc);
                    }

                    Collection<ObjectProperty> properties = od.getProperties();
                    if (!properties.isEmpty()) {
                        adoc.title4("字段");
                        parameterTable(properties, adoc);
                    }
                    break;
                case ENUM:
                    EnumDeclaration ed = (EnumDeclaration) declaration;
                    adoc.anchor(ed.getHash()).title3(ed.getName());
                    List<Property> constants = ed.getConstants();
                    if (!constants.isEmpty()) {
                        adoc.title4("常量列表");
                        parameterTable(constants, adoc);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void saveDeclaration(Declaration declaration, Map<String, Declaration> declarationMap) {
        DeclarationType type = declaration.getType();
        if (type == DeclarationType.OBJECT) {
            ObjectDeclaration od = (ObjectDeclaration) declaration;

            if (declarationMap.containsKey(od.getName())) {
                return;
            }
            declarationMap.put(od.getName(), od);

            for (Declaration typeArg : od.getTypeArgs()) {
                saveDeclaration(typeArg, declarationMap);
            }

            for (ObjectProperty property : od.getProperties()) {
                saveDeclaration(property.getDeclaration(), declarationMap);
            }

            return;
        }

        if (type == DeclarationType.ARRAY) {

            Declarations.ArrayDeclaration ad = (Declarations.ArrayDeclaration) declaration;
            Declaration componentType = ad.getComponentType();
            saveDeclaration(componentType, declarationMap);
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

    private void parameterTable(Collection<? extends Property> parameters, AdocWriter adoc) {

        adoc.tableBegin()
                .columnBegin().append("名称").space()
                .columnBegin().append("类型").space()
                .columnBegin().append("描述").dualNewLine();

        for (Property p : parameters) {
            adoc.columnBegin().append(p.getName()).newLine();
            adoc.columnBegin();
            appendType(p.getDeclaration(), adoc);
            adoc.newLine();
            adoc.columnBegin()
                    .appendLines(p.getDescription());
        }

        adoc.tableBegin();
    }


    private void appendType(Declaration d, AdocWriter adoc) {
        DeclarationType type = d.getType();
        switch (type) {
            case STRING:
                adoc.append("字符串");
                break;
            case NUMBER:
                adoc.append("数字");
                break;
            case TIMESTAMP:
                adoc.append("时间戳");
                break;
            case BOOLEAN:
                adoc.append("布尔");
                break;
            case ENUM_CONST:
                adoc.append("枚举常量");
                break;
            case DYNAMIC:
                adoc.append("任意对象");
                break;
            case TYPE_PARAMETER:
                Declarations.TypeArgDeclaration tad = (Declarations.TypeArgDeclaration) d;
                adoc.append("类型参数").append(tad.getName());
                break;
            case VOID:
                adoc.append("无");
                break;
            case ENUM:
                EnumDeclaration ed = (EnumDeclaration) d;
                adoc.link(ed.getHash(), ed.getName());
                break;
            case ARRAY:
                Declarations.ArrayDeclaration ad = (Declarations.ArrayDeclaration) d;
                Declaration cd = ad.getComponentType();
                adoc.append("Array<");
                appendType(cd, adoc);
                adoc.append('>');
                break;
            case OBJECT:
                ObjectDeclaration od = (ObjectDeclaration) d;
                List<Declaration> typeArgs = od.getTypeArgs();
                if (typeArgs.isEmpty()) {
                    adoc.link(od.getHash(), od.getName());
                } else {
                    adoc.link(od.getHash(), od.getName()).append('<');
                    for (int i = 0; i < typeArgs.size(); i++) {
                        appendType(typeArgs.get(i), adoc);
                        if (i != typeArgs.size() - 1) {
                            adoc.append(" ,");
                        }
                    }
                    adoc.append('>');
                }

                break;
            case UNKNOWN:
                adoc.append("未知类型");
                break;
        }
    }
}
