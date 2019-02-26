package com.chendayu.dydoc.testapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单测试 controller
 */
@RestController
public class SimpleController {

    /**
     * 测试方法
     * 并没有什么乱用
     *
     * @param i 假装我是一个参数
     */
    @GetMapping("/")
    public SimpleData testInt(@RequestParam int i) {
        return null;
    }
}
