package com.chendayu.dydoc.processor;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Resource {

    private String name;

    private String hash;

    private SortedSet<Action> actions = new TreeSet<>(Comparator.comparing(Action::getName));

    private String path;

    private List<String> description;

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

    public void setName(String name) {
        this.name = name;
    }

    public SortedSet<Action> getActions() {
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

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}

