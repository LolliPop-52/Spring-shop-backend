package com.example.spring_shop.service;

import javax.naming.AuthenticationException;
import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.dto.UserUpdateDTO;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.RefreshTokenDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {


    // Security

    //Create
    JwtAuthenticationDTO signIn(UserDTO userDTO)
            throws AuthenticationException;


    JwtAuthenticationDTO signUp(UserDTO userDTO)
            throws AuthenticationException;

    JwtAuthenticationDTO refreshToken(RefreshTokenDTO refreshTokenDTO)
            throws AuthenticationException;

    JwtAuthenticationDTO userUpdate(UserUpdateDTO userUpdateDTO) throws AuthenticationException;

    //CRUD

    UserDTO getUserById(Long id);

    UserDTO getUserByEmail(String email);

    String deleteUserById(Long id);

    ResponseEntity<String> confirmUser(String token);
}
