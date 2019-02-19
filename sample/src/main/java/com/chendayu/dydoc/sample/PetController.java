package com.chendayu.dydoc.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/pets")
public class PetController {


    @GetMapping
    public PetPage list() {
        return null;
    }
}
