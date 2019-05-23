package com.chendayu.c2d.processor.declaration;

import java.util.Collections;
import java.util.List;

public enum SimpleDeclaration implements Declaration {

    STRING(DeclarationType.STRING, "string"),
    NUMBER(DeclarationType.NUMBER, "number"),
    TIMESTAMP(DeclarationType.TIMESTAMP, "timestamp"),
    BOOLEAN(DeclarationType.BOOLEAN, "boolean"),
    ENUM_CONST(DeclarationType.ENUM_CONST, "enum const"),
    DYNAMIC(DeclarationType.DYNAMIC, "dynamic object"),
    FILE(DeclarationType.FILE, "file"),

    VOID(DeclarationType.VOID, "void"),
    UNKNOWN(DeclarationType.UNKNOWN, "unknown");

    private final DeclarationType type;

    private final List<String> description;

    SimpleDeclaration(DeclarationType type, String description) {
        this.type = type;
        this.description = Collections.singletonList(description);
    }

    @Override
    public DeclarationType getType() {
        return type;
    }

    @Override
    public List<String> getDescription() {
        return description;
    }
}
