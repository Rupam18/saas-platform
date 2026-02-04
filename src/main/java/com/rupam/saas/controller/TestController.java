package com.rupam.saas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/secure")
    public String secure() {
        return "You are authenticated. Welcome to the SaaS!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminOnly() {
        return "Welcome Admin 👑";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userOnly() {
        return "Welcome User";
    }
}

