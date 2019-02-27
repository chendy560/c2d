package com.chendayu.c2d.processor.declaration;

@DeclarationTest
public class GenericTestClasses {

    @DeclarationTest
    public void test(SomeData<String> sd1) {
    }

    public static class SomeData<T> {

        private T data;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
