package com.example.spring_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDTO {

    private String newName;

    private String email;
    private String newEmail;

    private String password;

    private String newPassword;
    private String newConfirmPassword;


}
