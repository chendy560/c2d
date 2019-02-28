package com.chendayu.c2d.processor.app;

import lombok.Data;

import java.util.Date;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest {

    /**
     * 又是一个纯粹测试用字段
     */
    private Date nonsense;

    /**
     * 用户数据
     */
    private User user;
}
