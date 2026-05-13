package com.example.spring_shop.service;

import javax.naming.AuthenticationException;

import com.example.spring_shop.dto.UserUpdateDTO;
import com.example.spring_shop.mail.MailService;
import com.example.spring_shop.mail.VerificationToken;
import com.example.spring_shop.repository.VerificationTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;



    private final JwtService jwtService;
    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public JwtAuthenticationDTO signIn(UserDTO userDTO)
            throws AuthenticationException {
        UserDTO signUserDTO = getSingInUser(userDTO);
        return jwtService.generateAuthToken(signUserDTO.getEmail());
    }

    @Override
    @Transactional
    public JwtAuthenticationDTO signUp(UserDTO userDTO)
            throws AuthenticationException {
        addUser(userDTO);
        return jwtService.generateAuthToken(userDTO.getEmail());
    }

    @Override
    @Transactional
    public JwtAuthenticationDTO userUpdate(UserUpdateDTO userUpdateDTO)
            throws AuthenticationException {
        User user = findByUserUpdateDTO(userUpdateDTO);

        if(!passwordEncoder.matches(userUpdateDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("uncorrected password");
        }
        if(userUpdateDTO.getNewEmail() != null){
            user.setEmail(userUpdateDTO.getNewEmail());
        }
        if(userUpdateDTO.getNewName() != null){
            user.setName(userUpdateDTO.getNewName());
        }
        if(userUpdateDTO.getNewPassword() != null){
            if(userUpdateDTO.getNewPassword().equals(userUpdateDTO.getNewConfirmPassword())){
                user.setPassword(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
            } else {
                throw new AuthenticationException("passwords don't match");
            }
        }
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

    private UserDTO getSingInUser(UserDTO userDTO) throws AuthenticationException {
        User user = userRepository.findFirstByEmail(userDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(userDTO.getEmail()));
        if(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())){
            return userMapper.toDTO(user);
        } else {
            throw new AuthenticationException("uncorrected password");
        }
    }

    @Override
    @Transactional
    public UserDTO getUserById(Long id) {
        User user =
                userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));
        return  userMapper.toDTO(user);
    }


    private void addUser(UserDTO userDTO) throws AuthenticationException {

        if(!(userDTO.getPassword().equals(userDTO.getConfirmPassword()))){
            throw new AuthenticationException("passwords don't match");
        }

        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new AuthenticationException("email has already been registered");
        }

        User user = userMapper.toEntity(userDTO);
        user.setRole(UserRole.CLIENT);

        user.setEnabled(false);

        Bucket bucket = new Bucket();
        user.setBucket(bucket);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        bucket.setUser(user);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
        mailService.sendVerificationEmail(user.getEmail(), token);

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
    public ResponseEntity<String> confirmUser(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(token));

        if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("The validity period has expired");
        }

        User user = verificationToken.getUser();

        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Account has been successfully verified!");
    }

    public User findByUserUpdateDTO(UserUpdateDTO userUpdateDTO)
        throws AuthenticationException {
        return userRepository.findFirstByEmail(userUpdateDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(userUpdateDTO.getEmail()));
    }



}
