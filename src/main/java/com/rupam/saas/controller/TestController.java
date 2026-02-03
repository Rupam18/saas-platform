package com.rupam.saas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/secure")
    public String secure() {
        return "You are authenticated. Welcome to the SaaS!";
    }
}
