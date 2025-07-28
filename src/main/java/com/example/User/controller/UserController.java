package com.example.User.controller;

import com.example.User.dto.request.UserUpdateRequest;
import com.example.User.dto.response.UserResponse;
import com.example.User.entity.User;
import com.example.User.dto.request.UserRequest;
import com.example.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(userUpdateRequest);
    }

    @PutMapping("/userStatus/{userId}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long userId) {
        return userService.deactivateUser(userId);
    }
}