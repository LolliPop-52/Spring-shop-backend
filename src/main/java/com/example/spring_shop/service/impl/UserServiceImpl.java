package com.example.spring_shop.service.impl;

import javax.naming.AuthenticationException;

import com.example.spring_shop.dto.UserUpdateDTO;
import com.example.spring_shop.exception_handler.RuntimeException.UserNotFoundException;
import com.example.spring_shop.exception_handler.RuntimeException.VerificationTokenNotFoundException;
import com.example.spring_shop.mail.MailService;
import com.example.spring_shop.mail.VerificationToken;
import com.example.spring_shop.repository.VerificationTokenRepository;
import com.example.spring_shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_shop.domain.Bucket;
import com.example.spring_shop.domain.User;
import com.example.spring_shop.domain.UserRole;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.mapper.UserMapper;
import com.example.spring_shop.repository.UserRepository;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.JwtService;
import com.example.spring_shop.security.RefreshTokenDTO;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
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

        User user = userRepository.findFirstByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException(userDTO.getEmail()));
        if(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())){
            UserDTO signUserDTO = userMapper.toDTO(user);
            return jwtService.generateAuthToken(signUserDTO.getEmail());
        } else {
            throw new AuthenticationException("uncorrected password");
        }
    }

    @Override
    @Transactional
    public JwtAuthenticationDTO signUp(UserDTO userDTO)
            throws AuthenticationException {

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

        return jwtService.generateAuthToken(user.getEmail());
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
                    .orElseThrow(() -> new UserNotFoundException(
                            jwtService.getEmailFromToken(refreshToken)));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    @Transactional
    public UserDTO getUserById(Long id) {
        User user =
                userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return  userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public String deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException(id);
        }
        return "User deleted";
    }

    @Override
    @Transactional
    public boolean confirmUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFoundException(token));

        User user = verificationToken.getUser();

        if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("<== [UserService.confirmUser() 400] Не удалось подтвердить почту '{}', истек срок действия ссылки (токена)", user.getEmail());
            return false;
        }

        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        log.info("==> [UserService.confirmUser() 200] Почта '{}' успешно подтверждена", user.getEmail());
        return true;
    }

    public User findByUserUpdateDTO(UserUpdateDTO userUpdateDTO)
        throws AuthenticationException {
        return userRepository.findFirstByEmail(userUpdateDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException(userUpdateDTO.getEmail()));
    }

}
