package com.example.chatwebproject.security.service;


import com.example.chatwebproject.model.entity.ERole;
import com.example.chatwebproject.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailImpl implements UserDetails {
    private final String username;
    private final Long id;
    @JsonIgnore
    private final String password;
    private final String email;
    private final GrantedAuthority authorities;

    public UserDetailImpl(String username, String password, String email, GrantedAuthority authorities, Long id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.id = id;
    }

    public static UserDetails build(User user) {
        return new UserDetailImpl(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                new SimpleGrantedAuthority(ERole.of(user.getRole().getRole().getId()).name()),
                user.getId()); // new SimpleGrantedAuthority(String)
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(this.authorities);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId(){return this.id;}
}
