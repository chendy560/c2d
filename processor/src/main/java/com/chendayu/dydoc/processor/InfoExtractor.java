package com.chendayu.dydoc.processor;

public abstract class InfoExtractor {

    protected final Toolbox toolbox;

    protected final Warehouse warehouse;

    public InfoExtractor(Toolbox toolbox, Warehouse warehouse) {
        this.toolbox = toolbox;
        this.warehouse = warehouse;
    }
}
