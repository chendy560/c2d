package com.chendayu.c2d.processor.util;

/**
 * 共享一个 {@link StringBuilder} 以略微提高性能
 * 因为 annotation-processor 是单线程跑下来的，所以简单做一个静态变量即可，不需要做 ThreadLocal
 */
public class StringBuilderHolder {

    /**
     * 不一定够用的默认长度…
     */
    public static final int DEFAULT_CAPACITY = 10 * 1024;

    private static final StringBuilder instance = new StringBuilder(DEFAULT_CAPACITY);

    private StringBuilderHolder() {
        // 工具类，不要实例化
    }

    /**
     * @return 共享的 {@link StringBuilder} 实例
     */
    public static StringBuilder resetAndGet() {
        instance.setLength(0);
        return instance;
    }
}
