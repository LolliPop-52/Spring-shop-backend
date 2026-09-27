package com.example.spring_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {
    private Long id;
    private String password;
    private String confirmPassword;
    private String name;
    private String email;
    private Long bucketId;
    private String role;
    private boolean enabled;

}
