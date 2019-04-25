package com.chendayu.c2d.processor.model;

/**
 * 类型的类型，一定程度上来说这套逻辑可以用 instanceof 实现
 */
public enum DeclarationType {

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
    DYNAMIC,

    /**
     * 类型参数
     */
    TYPE_PARAMETER,

    /**
     * void
     */
    VOID,

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
    OBJECT,

    /**
     * 文件
     */
    FILE,

    /**
     * 解析不出来的东西
     */
    UNKNOWN
}
