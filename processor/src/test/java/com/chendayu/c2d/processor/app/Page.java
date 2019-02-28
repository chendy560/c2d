package com.chendayu.c2d.processor.app;

import lombok.Value;

import java.util.List;

/**
 * 数据分页的一页
 *
 * @param <T> 分页中的数据的类型
 */
@Value
public class Page<T> {

    /**
     * 数据总量
     */
    private long count;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 本页数据
     */
    private List<T> items;
}
