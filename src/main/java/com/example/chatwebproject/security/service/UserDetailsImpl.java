package com.example.chatwebproject.security.service;


import com.example.chatwebproject.model.Account;
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
    private String username;
    private String phone;

    @JsonIgnore
    private String password;
    private GrantedAuthority authorities;

    public UserDetailsImpl(String username, String phone, String password, GrantedAuthority authorities) {
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Account account) {
        return new UserDetailsImpl(
                account.getUsername(),
                account.getPassword(),
                account.getPhone(),
                new SimpleGrantedAuthority(ERole.of(account.getRole().getRole().getId()).name())
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
}
