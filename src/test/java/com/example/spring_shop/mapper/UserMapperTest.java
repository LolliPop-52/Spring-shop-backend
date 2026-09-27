package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class UserMapperTest {

    private UserMapper userMapper;
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        user = TestDataFactory.createUser();
        userDTO = TestDataFactory.createUserDTO(dto -> dto.confirmPassword(null));
    }

    @Test
    @DisplayName("toDTO: Успешное преобразование User в UserDTO")
    void toDTO_Success() {
        UserDTO result = userMapper.toDTO(user);

        assertEquals(userDTO, result);
    }

    @Test
    @DisplayName("toDTO: Возврат null при передаче null")
    void toDTO_With_Null_Success() {
        UserDTO result = userMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("toEntity: Успешное преобразование UserDTO в User")
    void toEntity_Success() {
        User expectedUser = TestDataFactory.createUser(u -> u
                .role(null)
                .bucket(null)
                .enabled(false)
        );

        User result = userMapper.toEntity(userDTO);

        assertEquals(expectedUser, result);
    }

    @Test
    @DisplayName("toEntity: Возврат null при передаче null")
    void toEntity_With_Null_Success() {
        User result = userMapper.toEntity(null);

        assertNull(result);
    }
}
