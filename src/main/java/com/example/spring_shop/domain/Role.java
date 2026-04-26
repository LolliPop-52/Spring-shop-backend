package com.example.spring_shop.domain;

public enum Role {
    CLIENT, MANAGER, ADMIN;

    
    public String getPrefixName(){
        return "ROLE_" + this.name();
    }
}
