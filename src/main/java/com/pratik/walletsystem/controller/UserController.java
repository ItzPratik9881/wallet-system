package com.pratik.walletsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.pratik.walletsystem.dto.UserRegistrationRequest;
import com.pratik.walletsystem.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationRequest request) {

        return userService.registerUser(request);

    }
}