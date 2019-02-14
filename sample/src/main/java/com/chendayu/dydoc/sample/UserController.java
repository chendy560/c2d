package com.chendayu.dydoc.sample;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * 根据id获取用户
     *
     * @param id 用户id
     * @return 我也不知道啥玩意
     */
    @GetMapping("/{id}")
    public User get(@PathVariable String id) {
        return null;
    }

    @PostMapping()
    public void create(@RequestBody User user) {

    }
}
