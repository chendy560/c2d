package com.chendayu.c2d.processor.app;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    /**
     * 创建用户
     *
     * @param request        用户创建请求
     * @param token          测试用字段，假装自己是一个token
     * @param response       并没有用到的东西
     * @param servletRequest 并没有用到的东西
     * @return 创建后的数据
     */
    @PostMapping
    public User create(@RequestBody UserCreateRequest request, @RequestParam String token,
                       HttpServletResponse response, HttpServletRequest servletRequest) {
        return null;
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @param t  测试用，应该被忽略掉的东西
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestPart String t) {
    }

    /**
     * 更新/覆盖用户数据
     *
     * @param request 更新请求
     * @param id      用户id
     */
    @PutMapping("/{id}")
    public void overwrite(@RequestBody UserUpdateRequest request, @PathVariable Long id) {
    }

    /**
     * 局部更新用户数据
     *
     * @param user 用户数据
     * @param id   用户id
     */
    @PatchMapping("/{id}")
    public void update(@RequestBody User user, @PathVariable Long id) {
    }

    /**
     * 通过id获取用户
     *
     * @param id          用户id
     * @param showDeleted 是否展示被删除的数据
     * @return 指定id的用户数据
     */
    @GetMapping("/{id}")
    public User get(@PathVariable Long id, boolean showDeleted) {
        return null;
    }

    /**
     * 搜索用户
     *
     * @param request 搜索请求
     * @return 搜索结果的分页
     */
    @GetMapping("/search")
    public Page<User> search(UserSearchRequest request) {
        return null;
    }

    /**
     * 列举用户
     *
     * @param request 分页请求
     * @return 分页数据
     */
    @GetMapping
    public Page<User> list(PageRequest request) {
        return null;
    }
}
