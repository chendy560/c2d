package com.chendayu.c2d.processor.declaration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@DeclarationTest
public class JacksonTestSupport {

    @DeclarationTest
    public void test(TestData t) {

    }

    public static class TestData {

        @JsonIgnore
        private int age;

        /**
         * 用户名
         */
        @JsonProperty("username")
        private String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
