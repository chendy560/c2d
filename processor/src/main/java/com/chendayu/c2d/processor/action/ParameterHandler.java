package com.chendayu.c2d.processor.action;

import javax.lang.model.element.VariableElement;

/**
 * 参数处理器，抄袭 spring 的思路
 */
public interface ParameterHandler {

    /**
     * 处理参数
     *
     * @param action  参数所属的 action
     * @param element 参数的 element
     * @return 是否结束处理
     */
    boolean handleParameter(Action action, VariableElement element);
}
