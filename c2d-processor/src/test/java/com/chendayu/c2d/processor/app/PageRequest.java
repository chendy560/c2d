package com.chendayu.c2d.processor.app;

import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {

    /**
     * 第几页
     */
    private Integer p;

    /**
     * 返回多少条
     */
    private Integer n;
}
