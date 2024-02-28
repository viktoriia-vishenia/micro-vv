package org.micro.serviceclient.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/start")
public class HelloController {

    @GetMapping("/begin")
    public String test() {
        return "Hello, all";
    }


    @GetMapping("/user-panel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String hello() {
        return "Hello, User";
    }

    @GetMapping("/admin-panel")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String hello2() {
        return "JUST FOR ADMIN";
    }
}
