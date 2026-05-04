package com.example.spring_shop.controller;


import javax.naming.AuthenticationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.RefreshTokenDTO;
import com.example.spring_shop.security.UserCredentialsDTO;
import com.example.spring_shop.service.UserService;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDTO> signIn(
            @RequestBody UserCredentialsDTO userCredentialsDTO) throws AuthenticationException {
        return ResponseEntity.ok(userService.signIn(userCredentialsDTO));
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDTO refresh(@RequestBody RefreshTokenDTO refreshTokenDTO)
            throws Exception {
        return userService.refreshToken(refreshTokenDTO);
    }
    
}
