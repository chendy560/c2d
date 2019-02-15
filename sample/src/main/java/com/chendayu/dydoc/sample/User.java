package com.chendayu.dydoc.sample;

import java.util.List;

/**
 * 用户
 */
public class User {

    /**
     * 用户名
     * 我就是要在这里写一个多行
     */
    private String name;

    /**
     * 年龄
     */
    private int age;

    /**
     * ta的小伙伴们
     */
    private List<User> friends;


    private Dog dog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
