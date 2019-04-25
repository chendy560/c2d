package com.chendayu.c2d.processor.app;

import com.chendayu.c2d.processor.DocIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户，就是用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class User extends IdEntity {

    /**
     * 用户名
     */
    @JsonProperty("username")
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 应该是一个获取不到的东西
     */
    @JsonIgnore
    private Object youCannotSeeMe;

    @DocIgnore
    private String ignoreMe;

    /**
     * 是坑爹的弗兰兹呢
     */
    private List<User> friends;

    /**
     * 宠物们
     */
    private List<Pet> pets;
}
