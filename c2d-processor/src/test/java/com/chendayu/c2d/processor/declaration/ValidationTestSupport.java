package com.chendayu.c2d.processor.declaration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

@DeclarationTest
public class ValidationTestSupport {

    @DeclarationTest
    public void test(TestData t) {

    }

    public static class TestData {

        /**
         * 用户名
         */
        @JsonProperty("username")
        @NotNull
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
