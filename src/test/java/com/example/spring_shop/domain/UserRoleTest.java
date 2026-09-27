package com.example.spring_shop.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class UserRoleTest {

    private UserRole userRole;

    @Test
    @DisplayName("getPrefixName: Получение названия роли с префиксом ROLE_")
    void getPrefixName() {
        for (UserRole role : UserRole.values()){
            assertEquals("ROLE_" + role.name(), role.getPrefixName());

        }
    }
}