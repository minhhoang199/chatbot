package com.example.chatwebproject.security.service;


import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.ERole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String email;
    private final Long id;
    @JsonIgnore
    private final String password;
    private final GrantedAuthority authorities;

    public UserDetailsImpl(String username, String email, String password, GrantedAuthority authorities, Long id) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.id = id;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                new SimpleGrantedAuthority(ERole.of(user.getRole().getRole().getId()).name()),
                user.getId()
        );
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
