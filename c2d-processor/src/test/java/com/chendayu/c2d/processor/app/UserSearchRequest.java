package com.chendayu.c2d.processor.app;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户搜索请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchRequest extends PageRequest {

    /**
     * 搜索关键字，搜索用户名，可以指定多个
     */
    private List<String> name;

    /**
     * 最大年龄
     */
    private Integer maxAge;

    /**
     * 最小年龄
     */
    private Integer minAge;
}
