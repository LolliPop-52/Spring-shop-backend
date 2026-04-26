package com.example.spring_shop.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.example.spring_shop.dto.UserDTO;

@Service
public interface UserService extends UserDetailsService{
    boolean save(UserDTO userDTO);
}
