package com.chendayu.c2d.processor.app;

import java.util.Date;

import lombok.Data;

/**
 * 用户创建请求
 */
@Data
public class UserCreateRequest {

    /**
     * 纯粹测试用字段
     */
    private Date nonsense;

    /**
     * 用户数据
     */
    private User user;
}
