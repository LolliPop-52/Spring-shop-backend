package com.example.spring_shop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.spring_shop.domain.Role;
import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public boolean save(UserDTO userDTO) {
        if (!Objects.equals(userDTO.getPassword(), userDTO.getMatchingPassword())) {
            throw new RuntimeException("Password is not equals");
        }
        User user = User.builder().name(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword())).email(userDTO.getEmail())
                .role(Role.CLIENT).build();
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email = " + email + " not found"));

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getPrefixName()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), roles);
    }

}
