package com.chendayu.dydoc.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Resource {

    private String name;

    private String description = "";

    private List<Action> actions = Collections.emptyList();

    private String path;

    public Resource(String name) {
        this.name = name;
    }

    public void addAction(Action action) {
        if (actions.isEmpty()) {
            actions = new ArrayList<>();
        }
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

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

