package com.chendayu.dydoc.processor;

public enum ParamType {

    /**
     * 字符串
     */
    STRING,

    /**
     * 数字
     */
    NUMBER,

    /**
     * 时间戳
     */
    TIMESTAMP,

    /**
     * 布尔
     */
    BOOLEAN,

    /**
     * 枚举常量
     */
    ENUM_CONST,

    /**
     * 动态对象，即Map
     */
    DYNAMIC_OBJECT,

    /**
     * 类型参数
     */
    TYPE_PARAMETER,

    /**
     * 枚举
     */
    ENUM,

    /**
     * 数组
     */
    ARRAY,

    /**
     * 对象
     */
    OBJECT;
}
