package com.chendayu.dydoc.processor.declaration;

@DeclarationTest
public class InheritTestClasses {

    @DeclarationTest
    public Child test(SimpleChild sc) {
        return null;
    }


    public interface TestInterface1<T> {

        /**
         * @return 测试数据
         */
        T getData();
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
    public static class Child extends Parent implements TestInterface1<String>, TestInterface2 {

        private String id;

        /**
         * 年龄
         */
        private int age;

        /**
         * email
         */
        private String email;

        @Override
        public String getId() {
            return id;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String getData() {
            return email;
        }
    }

    public static class SimpleChild extends Parent {

        /**
         * id
         */
        private String id;

        public String getId() {
            return id;
        }
    }
}
