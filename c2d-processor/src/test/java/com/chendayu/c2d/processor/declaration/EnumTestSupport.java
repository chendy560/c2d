package com.chendayu.c2d.processor.declaration;

@DeclarationTest
public class EnumTestSupport {

    @DeclarationTest
    public void test(UserType userType) {

    }

    /**
     * 用户类型
     */
    public enum UserType {

        /**
         * 普通类型，级别1
         */
        NORMAL(1),

        /**
         * 管理员，级别2
         */
        ADMIN(0);

        private final int level;

        UserType(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}
