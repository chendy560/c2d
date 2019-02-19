package com.chendayu.dydoc.processor;

import java.util.List;

public abstract class DocElement {

    private final String name;

    private final String hash;

    private List<String> description;

    protected DocElement(String name) {
        this.name = name;
        this.hash = getPrefix() + Sha256.shortHash(name);
    }

    protected abstract String getPrefix();

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
