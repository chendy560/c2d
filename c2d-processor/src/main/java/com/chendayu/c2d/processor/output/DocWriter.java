package com.chendayu.c2d.processor.output;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public static final String TITLE_RESPONSE = "Response";
    public static final String TITLE_REQUEST = "Request";
    private static final String TITLE_RESOURCES = "Resources";
    private static final String SOURCE_TYPE_HTTP = "http";
    private static final String TITLE_PATH_VARIABLES = "Path Variables";
    private static final String TITLE_PARAMETERS = "Parameters";
    private static final String TITLE_REQUEST_BODY = "Request Body";
    private static final String TITLE_RESPONSE_BODY = "Response Body";
    private static final String TITLE_COMPONENTS = "Components";
    private static final String TITLE_OBJECTS = "Objects";
    private static final String TITLE_TYPE_PARAMETERS = "Type Parameters";
    private static final String TITLE_FIELDS = "Fields";
    private static final String TITLE_USED_IN_ACTION = "Used in Action";
    private static final String TITLE_ENUMS = "Enums";
    private static final String TITLE_CONST = "Const";
    private static final String TITLE_USED_IN_OBJECT = "Used in Object";
    private static final String TITLE_API_DOC = " API Doc";
    private static final String TABLE_COL = "3,7";
    private static final String COL_NAME = "Name";
    private static final String COL_DESCRIPTION = "Description";
    private static final String SMALL_BEGIN = " : [small]#";
    private static final char SMALL_END = '#';
    private static final String TYPE_STRING = "string";
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_TIMESTAMP = "timestamp";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_ENUM_CONST = "enum_const";
    private static final String TYPE_ANY = "any";
    private static final String TYPE_TYPE_PARAMETER = "type_parameter";
    private static final String TYPE_NONE = "none";
    private static final String TYPE_FILE = "file";
    private static final String ARRAY_BEGIN = "array<";
    private static final char ARRAY_END = '>';
    private static final char TYPE_ARG_BEGIN = '<';
    private static final String TYPE_ARG_SPLITTER = " ,";
    private static final char TYPE_ARG_END = '>';
    private static final String TYPE_UNKNOWN = "Unknown";
    private final AdocWriter adoc;

    private final HttpDataGenerator httpDataGenerator;

    public DocWriter(Writer writer) {
        this.adoc = new AdocWriter(writer);
        this.httpDataGenerator = new HttpDataGenerator();
    }

    public void printDoc(Warehouse warehouse) {

        adoc.safeLine();

        writeTitle(warehouse);
        writeResources(warehouse.getResources());
        writeDeclarations(warehouse);
    }

    private void writeTitle(Warehouse warehouse) {
        String applicationName = warehouse.getApplicationName();

        adoc.title0(applicationName + TITLE_API_DOC);
    }

    private void writeResources(Collection<Resource> resources) {
        adoc.title1(TITLE_RESOURCES);

        for (Resource resource : resources) {
            writeResource(resource);
        }
    }

    private void writeResource(Resource resource) {
        adoc.anchor(resource.getLink());
        adoc.title2(resource.getName());

        for (Action action : resource.getActions()) {
            writeAction(action);
        }
    }

    private void writeAction(Action action) {
        adoc.anchor(action.getLink());
        adoc.title3(action.getName());
        adoc.appendLines(action.getDescription());

        adoc.title4(TITLE_REQUEST);
        adoc.sourceCodeBegin(SOURCE_TYPE_HTTP);
        String request = httpDataGenerator.generateRequest(action);
        adoc.append(request);
        adoc.sourceCodeEnd();

        List<Property> pathVariables = action.getPathVariables();
        if (!pathVariables.isEmpty()) {
            adoc.title5(TITLE_PATH_VARIABLES);
            parameterTable(pathVariables);
        }

        List<Property> urlParameters = action.getUrlParameters();
        if (!urlParameters.isEmpty()) {
            adoc.title5(TITLE_PARAMETERS);
            parameterTable(urlParameters);
        }

        Property requestBody = action.getRequestBody();
        if (requestBody != null) {
            adoc.title5(TITLE_REQUEST_BODY);
            writeType(requestBody.getDeclaration());
        }

        adoc.dualNewLine();

        adoc.title4(TITLE_RESPONSE);
        adoc.sourceCodeBegin(SOURCE_TYPE_HTTP);
        String response = httpDataGenerator.generateResponse(action);
        adoc.append(response);
        adoc.sourceCodeEnd();

        Property responseBody = action.getResponseBody();
        if (responseBody != null) {
            adoc.title5(TITLE_RESPONSE_BODY);
            writeType(responseBody.getDeclaration());
        }

        adoc.dualNewLine();
    }

    private void writeDeclarations(Warehouse warehouse) {
        adoc.title1(TITLE_COMPONENTS);
        writeObjects(warehouse);
        writeEnums(warehouse);
    }

    private void writeObjects(Warehouse warehouse) {
        adoc.title2(TITLE_OBJECTS);

        for (NestedDeclaration nested : warehouse.getUsedNestedDeclaration()) {
            adoc.anchor(nested.getLink());
            adoc.title3(nested.getShortName());

            adoc.appendLines(nested.getDescription());
            List<TypeVarDeclaration> typeParameters = nested.getTypeParameters();
            if (!typeParameters.isEmpty()) {
                adoc.title4(TITLE_TYPE_PARAMETERS);
                parameterTable(typeParameters);
            }

            Collection<Property> properties = nested.accessibleProperties();
            if (!properties.isEmpty()) {
                adoc.title4(TITLE_FIELDS);
                parameterTable(properties);
            }

            writeUsedInAction(nested.getUsedInAction());
            writeUsedInObject(nested.getUsedInDeclaration());
        }
    }

    private void writeUsedInAction(Set<Action> usedInAction) {
        if (!usedInAction.isEmpty()) {
            adoc.title4(TITLE_USED_IN_ACTION);
            for (Action action : usedInAction) {
                adoc.link(action.getLink(), action.getFullName());
                adoc.appendSpace();
            }
            adoc.dualNewLine();
        }
    }

    private void writeEnums(Warehouse warehouse) {
        adoc.title2(TITLE_ENUMS);
        for (EnumDeclaration declaration : warehouse.getUsedEnumDeclaration()) {
            adoc.anchor(declaration.getLink());
            adoc.title3(declaration.getName());
            List<Property> constants = declaration.getConstants();
            if (!constants.isEmpty()) {
                adoc.title4(TITLE_CONST);
                parameterTable(constants);
            }

            writeUsedInObject(declaration.getUsedInDeclaration());
        }
    }

    private void writeUsedInObject(Set<NestedDeclaration> usedInDeclaration) {
        if (!usedInDeclaration.isEmpty()) {
            adoc.title4(TITLE_USED_IN_OBJECT);
            for (NestedDeclaration nestedDeclaration : usedInDeclaration) {
                adoc.link(nestedDeclaration.getLink(), nestedDeclaration.getShortName());
                adoc.appendSpace();
            }
            adoc.dualNewLine();
        }
    }

    private void parameterTable(List<TypeVarDeclaration> parameters) {

        adoc.col(TABLE_COL);
        adoc.tableBoundary();
        adoc.columnBegin();
        adoc.append(COL_NAME);
        adoc.appendSpace();
        adoc.columnBegin();
        adoc.append(COL_DESCRIPTION);
        adoc.dualNewLine();

        for (TypeVarDeclaration p : parameters) {
            adoc.columnBegin();
            adoc.appendBoldMonospace(p.getName());
            adoc.append(SMALL_BEGIN);
            writeType(p);
            adoc.append(SMALL_END);
            adoc.newLine();
            adoc.columnBegin();
            adoc.appendLines(p.getDescription());
        }

        adoc.tableBoundary();
    }

    private void parameterTable(Collection<? extends Property> parameters) {

        adoc.col(TABLE_COL);
        adoc.tableBoundary();
        adoc.columnBegin();
        adoc.append(COL_NAME);
        adoc.appendSpace();
        adoc.columnBegin();
        adoc.append(COL_DESCRIPTION);
        adoc.dualNewLine();

        for (Property p : parameters) {
            adoc.columnBegin();
            adoc.appendBoldMonospace(p.getDisplayName());
            adoc.append(SMALL_BEGIN);
            writeType(p.getDeclaration());
            adoc.append(SMALL_END);
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
                adoc.append(TYPE_STRING);
                break;
            case NUMBER:
                adoc.append(TYPE_NUMBER);
                break;
            case TIMESTAMP:
                adoc.append(TYPE_TIMESTAMP);
                break;
            case BOOLEAN:
                adoc.append(TYPE_BOOLEAN);
                break;
            case ENUM_CONST:
                adoc.append(TYPE_ENUM_CONST);
                break;
            case DYNAMIC:
                adoc.append(TYPE_ANY);
                break;
            case TYPE_PARAMETER:
                adoc.append(TYPE_TYPE_PARAMETER);
                break;
            case VOID:
                adoc.append(TYPE_NONE);
                break;
            case FILE:
                adoc.append(TYPE_FILE);
                break;
            case ENUM:
                EnumDeclaration ed = (EnumDeclaration) d;
                adoc.link(ed.getLink(), ed.getName());
                break;
            case ARRAY:
                ArrayDeclaration ad = (ArrayDeclaration) d;
                Declaration cd = ad.getItemType();
                adoc.append(ARRAY_BEGIN);
                writeType(cd);
                adoc.append(ARRAY_END);
                break;
            case OBJECT:
                NestedDeclaration od = (NestedDeclaration) d;
                List<Declaration> typeArgs = od.getTypeArguments();
                if (typeArgs.isEmpty()) {
                    adoc.link(od.getLink(), od.getShortName());
                } else {
                    adoc.link(od.getLink(), od.getShortName());
                    adoc.append(TYPE_ARG_BEGIN);
                    for (int i = 0; i < typeArgs.size(); i++) {
                        writeType(typeArgs.get(i));
                        if (i != typeArgs.size() - 1) {
                            adoc.append(TYPE_ARG_SPLITTER);
                        }
                    }
                    adoc.append(TYPE_ARG_END);
                }

                break;
            case UNKNOWN:
                adoc.append(TYPE_UNKNOWN);
                break;
        }
    }
}
