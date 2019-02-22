package com.chendayu.dydoc.processor;

/**
 * 类似于 class 的东西
 */
public interface Declaration {

    Declaration STRING = () -> Type.STRING;
    Declaration NUMBER = () -> Type.NUMBER;
    Declaration TIMESTAMP = () -> Type.TIMESTAMP;
    Declaration BOOLEAN = () -> Type.BOOLEAN;
    Declaration ENUM_CONST = () -> Type.ENUM_CONST;
    Declaration DYNAMIC = () -> Type.DYNAMIC;
    Declaration ENUM = () -> Type.ENUM;
    Declaration VOID = () -> Type.VOID;
    Declaration TYPE_PARAMETER = () -> Type.TYPE_PARAMETER;

    static ArrayDeclaration arrayOf(Declaration declaration) {
        return () -> declaration;
    }

    Type getType();

    enum Type {

        /**
         * 字符串
         */
        STRING,

        /**
         * 数字
         */
        NUMBER,

        /**
         * 时间戳
         */
        TIMESTAMP,

        /**
         * 布尔
         */
        BOOLEAN,

        /**
         * 枚举常量
         */
        ENUM_CONST,

        /**
         * 动态对象，即Map
         */
        DYNAMIC,

        /**
         * 类型参数
         */
        TYPE_PARAMETER,

        /**
         * void
         */
        VOID,

        /**
         * 枚举
         */
        ENUM,

        /**
         * 数组
         */
        ARRAY,

        /**
         * 对象
         */
        OBJECT
    }

    interface ArrayDeclaration extends Declaration {

        @Override
        default Type getType() {
            return Type.ARRAY;
        }

        Declaration getComponentType();
    }
}
