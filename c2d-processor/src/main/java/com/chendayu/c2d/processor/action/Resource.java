package com.chendayu.c2d.processor.action;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

import com.chendayu.c2d.processor.util.NameConversions;

/**
 * 本意是 "被操作的资源"，但是在很多实际项目中（特别是涉及到UI层的项目中），含义更类似于"一组API"
 */
public class Resource {

    /**
     * 资源名称
     */
    private final String name;

    /**
     * 链接，用于文档中跳转使用
     */
    private final String link;

    /**
     * 资源的操作，按字母顺序排序
     */
    private final TreeMap<String, Action> actions;

    /**
     * 资源的根路径
     */
    private String path;

    public Resource(String name) {
        this.name = name;
        this.link = NameConversions.resourceLink(name);
        this.actions = new TreeMap<>(Comparator.naturalOrder());
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public void addAction(Action action) {
        actions.put(action.getName(), action);
    }

    public boolean containsAction(String actionName) {
        return actions.containsKey(actionName);
    }

    public Collection<Action> getActions() {
        return actions.values();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
