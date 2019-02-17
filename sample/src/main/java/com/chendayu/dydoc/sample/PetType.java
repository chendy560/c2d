package com.chendayu.dydoc.sample;

/**
 * 宠物类型
 */
public enum PetType {

    /**
     * 猫，cat
     */
    CAT("cat"),

    /**
     * 狗，dog
     */
    DOG("dog");

    private final String name;

    PetType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
