package com.rupam.saas.controller;

import com.rupam.saas.dto.UserResponse;
import com.rupam.saas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication authentication) {
        return userService.getUserProfile(authentication.getName());
    }

    @GetMapping
    public List<UserResponse> getAllUsers(Authentication authentication) {
        // Implement logic in service to filter by company of the logged-in user
        return userService.getAllUsersByCompany(authentication.getName());
    }
}
