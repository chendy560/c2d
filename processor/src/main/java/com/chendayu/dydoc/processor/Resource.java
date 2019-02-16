package com.chendayu.dydoc.processor;

import java.util.Comparator;
import java.util.TreeSet;

public class Resource {

    private String name;

    private String description = "";

    private String hash;

    private TreeSet<Action> actions = new TreeSet<>(Comparator.comparing(Action::getName));

    private String path;

    public Resource(String name) {
        this.name = Utils.upperCaseFirst(name);
        this.hash = 'r' + Sha256.shortHash(name);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TreeSet<Action> getActions() {
        return actions;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHash() {
        return hash;
    }
}

