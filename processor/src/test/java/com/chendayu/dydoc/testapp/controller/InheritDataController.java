package com.chendayu.dydoc.testapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InheritDataController {

    @GetMapping("/test")
    public Child test() {
        return null;
    }

    public interface TestInterface1 {

        /**
         * @return 电子邮件
         */
        String getEmail();
    }

    public interface TestInterface2 {

        /**
         * @return 用户id
         */
        String getId();
    }

    public static class Parent {

        /**
         * 姓名
         */
        private String name;

        public String getName() {
            return name;
        }
    }

    /**
     * 子类
     */
    public static class Child extends Parent implements TestInterface1, TestInterface2 {

        /**
         * id
         */
        private String id;

        /**
         * 年龄
         */
        private int age;

        /**
         * email
         */
        private String email;

        public String getId() {
            return id;
        }

        public int getAge() {
            return age;
        }

        public String getEmail() {
            return email;
        }
    }
}
