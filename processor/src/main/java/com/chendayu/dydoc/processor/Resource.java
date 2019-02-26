package com.chendayu.dydoc.processor;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 类似于 entity 的概念，API中的被操作对象
 * 对同种 Resource 的 Action 应该在同一个 Controller 中
 */
public class Resource {

    private final String name;

    private SortedSet<Action> actions = new TreeSet<>(Comparator.comparing(Action::getName));

    private String path;

    public Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAction(Action action) {
        actions.add(action);
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
}

