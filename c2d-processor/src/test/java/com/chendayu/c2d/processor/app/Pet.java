package com.chendayu.c2d.processor.app;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 宠物，可能是🐈，可能是🐶
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Pet extends IdEntity {

    /**
     * 宠物的名字
     */
    private String name;

    /**
     * 宠物的生日，啊为什么是时间戳呢，因为现在还没有日期类型啊
     */
    private Instant birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 类型
     */
    private PetType type;
}
