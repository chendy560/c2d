package com.chendayu.dydoc.testapp.controller;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 lombok 相关注解
 */
@RestController
public class LombokTestController {

    @GetMapping("/test1")
    public LombokDataTestClass test1() {
        return null;
    }

    @GetMapping("/test2")
    public LombokGetterTestClass test2() {
        return null;
    }


    /**
     * 测试数据
     */
    @Data
    public static class LombokDataTestClass {

        /**
         * 姓名
         */
        private String name;

        /**
         * 年龄
         */
        private int age;
    }

    /**
     * 另一个测试数据
     */
    @Getter
    public static class LombokGetterTestClass {

        /**
         * 电子邮件地址
         */
        private String email;
    }
}


