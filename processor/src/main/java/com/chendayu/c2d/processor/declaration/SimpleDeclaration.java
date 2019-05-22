package com.chendayu.c2d.processor.declaration;

public enum SimpleDeclaration implements Declaration {

    STRING(DeclarationType.STRING),
    NUMBER(DeclarationType.NUMBER),
    TIMESTAMP(DeclarationType.TIMESTAMP),
    BOOLEAN(DeclarationType.BOOLEAN),
    ENUM_CONST(DeclarationType.ENUM_CONST),
    DYNAMIC(DeclarationType.DYNAMIC),
    FILE(DeclarationType.FILE),

    VOID(DeclarationType.VOID),
    UNKNOWN(DeclarationType.UNKNOWN);

    private final DeclarationType type;

    SimpleDeclaration(DeclarationType type) {
        this.type = type;
    }

    @Override
    public DeclarationType getType() {
        return type;
    }
}
