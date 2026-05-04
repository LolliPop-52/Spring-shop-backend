package com.example.spring_shop.domain;

public enum UserRole {
    CLIENT, MANAGER, ADMIN;

    public String getPrefixName() {
        return "ROLE_" + this.name();
    }
}
