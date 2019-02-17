package com.chendayu.dydoc.sample;

import java.util.List;

/**
 * 用户
 * 一定要有用户
 */
public class User {

    /**
     * 用户名
     */
    private String name;

    /**
     * 年龄
     */
    private int age;

    /**
     * 性别
     * 1 -> ♂
     * 2 -> ♀
     * 3 -> ?
     */
    private int gender;
    /**
     * ta的小伙伴们
     */
    private List<User> friends;

    /**
     * 宠物
     */
    private Pet pet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
