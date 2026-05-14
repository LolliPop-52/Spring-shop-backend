package com.example.spring_shop.controller;

import com.example.spring_shop.dto.UserUpdateDTO;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.service.UserService;

import lombok.RequiredArgsConstructor;

import javax.naming.AuthenticationException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(Long.parseLong(id)));
    }

    @PostMapping("/update")
    public ResponseEntity<JwtAuthenticationDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO)
            throws AuthenticationException {
        return ResponseEntity.ok(userService.userUpdate(userUpdateDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserByEmail(userDetails.getUsername()));
    }

}

