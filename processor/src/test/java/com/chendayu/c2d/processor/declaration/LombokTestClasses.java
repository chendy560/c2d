package com.chendayu.c2d.processor.declaration;

import lombok.Data;

@DeclarationTest
public class LombokTestClasses {

    @DeclarationTest
    public DataTestClass test(GetterTestClass g) {
        return null;
    }

    /**
     * Data注解测试数据
     */
    @Data
    public static class DataTestClass {

        /**
         * 名字
         */
        private String name;

        /**
         * 年龄
         */
        private int age;
    }

    /**
     * Getter注解测试数据
     */
    @Data
    public static class GetterTestClass {

        /**
         * 分数
         */
        private double score;

        /**
         * 是否通过
         */
        private boolean passed;
    }
}
