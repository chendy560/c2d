package com.chendayu.c2d.processor.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class PathTestController extends BaseController {

    @GetMapping
    public void test() {

    }
}
