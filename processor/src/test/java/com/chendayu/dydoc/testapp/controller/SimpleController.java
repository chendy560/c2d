package com.chendayu.dydoc.testapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单测试 controller
 */
@RestController
public class SimpleController {

    /**
     * 测试方法
     *
     * @param i 并没有什么意义的参数
     */
    @GetMapping("/")
    public TestResponse testInt(@RequestParam int i) {
        return null;
    }

    /**
     * 我是测试数据
     */
    public static class TestResponse {

        /**
         * 测试用名字
         */
        private String name;

        /**
         * 测试用年龄
         */
        private int age;

        /**
         * 另一个测试
         *
         * @return 名字
         */
        public String getName() {
            return name;
        }

        /**
         * 另一个测试
         *
         * @return 年龄
         */
        public int getAge() {
            return age;
        }
    }
}
