package com.chendayu.c2d.processor.output;

import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.declaration.Declaration;

import java.io.Writer;
import java.util.TreeMap;

public class DocWriter {

    private final TreeMap<String, Declaration> declarationMap = new TreeMap<>();

    private final AdocWriter adoc;

    public DocWriter(Writer writer) {
        this.adoc = new AdocWriter(writer);
    }

    public void printDoc(Warehouse warehouse) {
//
//        writeTitle(warehouse);
//
//        writeResources(warehouse.getResources());
//
//        writeDeclarations();
    }
//
//    private void writeResources(Collection<Resource> resources) {
//        adoc.title1("资源");
//
//        for (Resource resource : resources) {
//            adoc.title2(resource.getName());
//
//            SortedSet<Action> actions = resource.getActions();
//            for (Action action : actions) {
//                writeActions(action);
//            }
//        }
//    }
//
//    private void writeActions(Action action) {
//        String name = action.getName();
//        adoc.title3(name);
//        adoc.appendLines(action.getDescription());
//
//        adoc.sourceCodeBegin("http")
//                .append(action.getMethod().name()).space().append(action.getPath())
//                .newLine()
//                .sourceCodeEnd();
//
//        List<Property> pathVariables = action.getPathVariables();
//        if (!pathVariables.isEmpty()) {
//            adoc.title4("路径参数");
//            for (Property pathVariable : pathVariables) {
//                saveDeclaration(pathVariable.getDeclaration());
//            }
//            parameterTable(pathVariables);
//        }
//
//        List<Property> urlParameters = action.getUrlParameters();
//        if (!urlParameters.isEmpty()) {
//            adoc.title4("URL参数");
//            for (Property property : urlParameters) {
//                saveDeclaration(property.getDeclaration());
//            }
//            parameterTable(urlParameters);
//        }
//
//        Property requestBody = action.getRequestBody();
//        if (requestBody != null) {
//            adoc.title4("请求Body");
//            writeType(requestBody.getDeclaration());
//            saveDeclaration(requestBody.getDeclaration());
//            adoc.dualNewLine();
//        }
//
//        Property responseBody = action.getResponseBody();
//        if (responseBody != null) {
//            adoc.title4("响应");
//            writeType(responseBody.getDeclaration());
//            saveDeclaration(responseBody.getDeclaration());
//            adoc.dualNewLine();
//        }
//
//        adoc.dualNewLine();
//    }
//
//    private void writeDeclarations() {
//        adoc.title1("对象结构");
//
//        for (Declaration declaration : declarationMap.values()) {
//            DeclarationType type = declaration.getType();
//            switch (type) {
//                case OBJECT:
//                    NestedDeclaration od = (NestedDeclaration) declaration;
//                    adoc.anchor(od.getHash()).title3(od.getName());
//
//                    adoc.appendLines(od.getDescription());
//                    List<Property> typeParameters = od.getTypeParameters();
//                    if (!typeParameters.isEmpty()) {
//                        adoc.title4("类型参数");
//                        parameterTable(typeParameters);
//                    }
//
//                    Collection<Property> properties = od.allProperties();
//                    if (!properties.isEmpty()) {
//                        adoc.title4("字段");
//                        parameterTable(properties);
//                    }
//                    break;
//                case ENUM:
//                    EnumDeclaration ed = (EnumDeclaration) declaration;
//                    adoc.anchor(ed.getHash()).title3(ed.getName());
//                    List<Property> constants = ed.getConstants();
//                    if (!constants.isEmpty()) {
//                        adoc.title4("常量列表");
//                        parameterTable(constants);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    private void writeTitle(Warehouse warehouse) {
//        String applicationName = warehouse.getApplicationName();
//
//        adoc.title0(applicationName + " API 文档");
//    }
//
//    private void saveDeclaration(Declaration declaration) {
//        DeclarationType type = declaration.getType();
//        if (type == DeclarationType.OBJECT) {
//            NestedDeclaration od = (NestedDeclaration) declaration;
//
//            if (declarationMap.containsKey(od.getName()) && od.getTypeArgs().isEmpty()) {
//                return;
//            }
//            declarationMap.put(od.getName(), od);
//
//            for (Declaration typeArg : od.getTypeArgs()) {
//                saveDeclaration(typeArg);
//            }
//
//            for (Property property : od.allProperties()) {
//                saveDeclaration(property.getDeclaration());
//            }
//
//            return;
//        }
//
//        if (type == DeclarationType.ARRAY) {
//
//            ArrayDeclaration ad = (ArrayDeclaration) declaration;
//            Declaration componentType = ad.getItemType();
//            saveDeclaration(componentType);
//            return;
//        }
//
//        if (type == DeclarationType.ENUM) {
//            EnumDeclaration ed = (EnumDeclaration) declaration;
//
//            if (declarationMap.containsKey(ed.getName())) {
//                return;
//            }
//
//            declarationMap.put(ed.getName(), ed);
//        }
//    }
//
//    private void parameterTable(Collection<? extends Property> parameters) {
//
//        adoc.tableBegin()
//                .columnBegin().append("名称").space()
//                .columnBegin().append("类型").space()
//                .columnBegin().append("描述").dualNewLine();
//
//        for (Property p : parameters) {
//            adoc.columnBegin().append(p.getDisplayName()).newLine();
//            adoc.columnBegin();
//            writeType(p.getDeclaration());
//            adoc.newLine();
//            adoc.columnBegin()
//                    .appendLines(p.getDescription());
//        }
//
//        adoc.tableBegin();
//    }
//
//
//    private void writeType(Declaration d) {
//        DeclarationType type = d.getType();
//        switch (type) {
//            case STRING:
//                adoc.append("字符串");
//                break;
//            case NUMBER:
//                adoc.append("数字");
//                break;
//            case TIMESTAMP:
//                adoc.append("时间戳");
//                break;
//            case BOOLEAN:
//                adoc.append("布尔");
//                break;
//            case ENUM_CONST:
//                adoc.append("枚举常量");
//                break;
//            case DYNAMIC:
//                adoc.append("任意对象");
//                break;
//            case TYPE_PARAMETER:
//                TypeVarDeclaration tad = (TypeVarDeclaration) d;
//                adoc.append("类型参数").append(tad.getName());
//                break;
//            case VOID:
//                adoc.append("无");
//                break;
//            case FILE:
//                adoc.append("文件");
//                break;
//            case ENUM:
//                EnumDeclaration ed = (EnumDeclaration) d;
//                adoc.link(ed.getHash(), ed.getName());
//                break;
//            case ARRAY:
//                ArrayDeclaration ad = (ArrayDeclaration) d;
//                Declaration cd = ad.getItemType();
//                adoc.append("Array<");
//                writeType(cd);
//                adoc.append('>');
//                break;
//            case OBJECT:
//                NestedDeclaration od = (NestedDeclaration) d;
//                List<Declaration> typeArgs = od.getTypeArgs();
//                if (typeArgs.isEmpty()) {
//                    adoc.link(od.getHash(), od.getName());
//                } else {
//                    adoc.link(od.getHash(), od.getName()).append('<');
//                    for (int i = 0; i < typeArgs.size(); i++) {
//                        writeType(typeArgs.get(i));
//                        if (i != typeArgs.size() - 1) {
//                            adoc.append(" ,");
//                        }
//                    }
//                    adoc.append('>');
//                }
//
//                break;
//            case UNKNOWN:
//                adoc.append("未知类型");
//                break;
//        }
//    }
}
