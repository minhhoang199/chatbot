package com.example.chatwebproject.model;

import lombok.Getter;

@Getter
public enum ERole {
    ROLE_USER(1000),
    ROLE_ADMIN(3000);

    public int id;

    ERole(int id) {
        this.id = id;
    }

    public static ERole of(int id) {
        switch (id) {
            case 1000:
                return ROLE_USER;
            case 3000:
                return ROLE_ADMIN;
            default:
                throw new IllegalArgumentException("Unknown Role: " + id);
        }
    }
}
