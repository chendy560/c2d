package com.chendayu.dydoc.processor;

enum ParameterType {

    STRING("字符串", true),
    NUMBER("数字", true),
    TIMESTAMP("时间戳", true),
    BOOLEAN("布尔", true),
    ENUM_CONST("枚举值", true),

    ENUM("枚举", false),
    ARRAY("数组", false),
    OBJECT("对象", false);

    private final String name;

    private final boolean simpleType;

    ParameterType(String name, boolean simpleType) {
        this.name = name;
        this.simpleType = simpleType;
    }

    public String getName() {
        return name;
    }

    public boolean isSimpleType() {
        return simpleType;
    }
}
