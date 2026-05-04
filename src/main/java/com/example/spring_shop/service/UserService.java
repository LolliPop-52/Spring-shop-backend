package com.example.spring_shop.service;

import javax.naming.AuthenticationException;
import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.RefreshTokenDTO;
import com.example.spring_shop.security.UserCredentialsDTO;

public interface UserService {


    // Secutiry
    JwtAuthenticationDTO signIn(UserCredentialsDTO userCredentialsDTO)
            throws AuthenticationException;

    JwtAuthenticationDTO refreshToken(RefreshTokenDTO refreshTokenDTO)
            throws AuthenticationException;


    // CRUD
    String addUser(UserDTO user);

    UserDTO getUserById(Long id);

    UserDTO getUserByEmail(String email);

    String updateUser(UserDTO user);

    String deleteUserById(Long id);

    User findByCredentials(UserCredentialsDTO userCredentialsDTO) throws AuthenticationException;
}
