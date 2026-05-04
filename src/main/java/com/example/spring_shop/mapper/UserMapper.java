package com.example.spring_shop.mapper;


import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user){
        if (user == null){
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole().name())
                .bucketId(user.getBucket().getId())
                .build();
    }

    public User toEntity(UserDTO userDTO){
        if(userDTO == null){
            return null;
        }
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .build();
    }
}
