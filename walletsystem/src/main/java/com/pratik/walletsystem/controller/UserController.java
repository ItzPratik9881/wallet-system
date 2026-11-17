package com.pratik.walletsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pratik.walletsystem.dto.ApiResponse;
import com.pratik.walletsystem.dto.UserRegistrationRequest;
import com.pratik.walletsystem.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user registration and profile")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account along with an active zero-balance wallet")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        String result = userService.registerUser(request);
        return new ResponseEntity<>(
                ApiResponse.success(result, "User registered successfully"),
                HttpStatus.CREATED
        );
    }
}
