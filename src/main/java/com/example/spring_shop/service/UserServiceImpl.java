package com.example.spring_shop.service;

import javax.naming.AuthenticationException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_shop.domain.Bucket;
import com.example.spring_shop.domain.User;
import com.example.spring_shop.domain.UserRole;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.mapper.UserMapper;
import com.example.spring_shop.repository.UserRepository;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.JwtService;
import com.example.spring_shop.security.RefreshTokenDTO;
import com.example.spring_shop.security.UserCredentialsDTO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public JwtAuthenticationDTO signIn(UserCredentialsDTO userCredentialsDTO)
            throws AuthenticationException {
        User user = findByCredentials(userCredentialsDTO);
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    @Transactional
    public JwtAuthenticationDTO refreshToken(RefreshTokenDTO refreshTokenDTO)
            throws AuthenticationException {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = userRepository.findFirstByEmail(jwtService.getEmailFromToken(refreshToken))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            jwtService.getEmailFromToken(refreshToken)));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user =
                userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));
        return  userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public String addUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setRole(UserRole.CLIENT);
        Bucket bucket = new Bucket();
        user.setBucket(bucket);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        bucket.setUser(user);
        userRepository.save(user);
        return "User added";
    }

    @Override
    @Transactional
    public String updateUser(UserDTO userDTO) {

        Long id = userDTO.getId();
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return "User upadated";
    }

    @Override
    @Transactional
    public String deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(id);
        }
        return "User deleted";
    }

    @Override
    @Transactional
    public User findByCredentials(UserCredentialsDTO userCredentialsDTO)
            throws AuthenticationException {
        User user = userRepository.findFirstByEmail(userCredentialsDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(userCredentialsDTO.getEmail()));
        if (passwordEncoder.matches(userCredentialsDTO.getPassword(), user.getPassword())) {
            return user;
        }
        throw new AuthenticationException("Email or password is not correct");
    }



}
