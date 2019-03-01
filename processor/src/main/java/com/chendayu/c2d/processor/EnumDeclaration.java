package com.chendayu.c2d.processor;

import java.util.List;

public class EnumDeclaration implements Declaration {

    private final String qualifiedName;

    private final String name;

    private final String hash;

    private final List<Property> constants;

    private final List<String> description;

    public EnumDeclaration(String name, String qualifiedName,
                           List<Property> constants, List<String> description) {
        this.name = name;
        this.hash = "d" + Utils.shortHash(qualifiedName);
        this.qualifiedName = qualifiedName;
        this.constants = constants;
        this.description = description;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public List<Property> getConstants() {
        return constants;
    }

    public List<String> getDescription() {
        return description;
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.ENUM;
    }
}
