package com.example.spring_shop.controller;


import javax.naming.AuthenticationException;

import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.mail.VerificationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<JwtAuthenticationDTO> signIn(@RequestBody UserDTO userDTO)
            throws AuthenticationException {
        return ResponseEntity.ok(userService.signIn(userDTO));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationDTO> signUp(@RequestBody UserDTO userDTO)
            throws AuthenticationException {
        return ResponseEntity.ok(userService.signUp(userDTO));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token){
        return userService.confirmUser(token);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDTO refresh(@RequestBody RefreshTokenDTO refreshTokenDTO)
            throws Exception {
        return userService.refreshToken(refreshTokenDTO);
    }
    
}
