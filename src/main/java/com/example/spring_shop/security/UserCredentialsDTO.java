package com.example.spring_shop.security;

import lombok.Data;

@Data
public class UserCredentialsDTO {
    private String email;
    private String password;
}
