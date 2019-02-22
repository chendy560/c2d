package com.chendayu.dydoc.processor;

import java.util.List;

/**
 * 接口方法的参数和返回值，以及他们可能嵌套的其他类
 * <p>
 * 实在想不到叫啥，最后就起了这么个名字
 */
public abstract class Declaration {

    /**
     * 参数或者字段的名字，一些东西没有名字，比如方法返回值
     */
    private final String name;

    /**
     * 参数或者字段的描述，即注释
     */
    private final List<String> description;

    public Declaration(List<String> description) {
        this.name = null;
        this.description = description;
    }

    public Declaration(String name, List<String> description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public abstract ParamType getType();
}
