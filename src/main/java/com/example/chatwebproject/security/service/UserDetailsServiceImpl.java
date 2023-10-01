package com.example.chatwebproject.security.service;

import com.example.chatwebproject.model.Account;
import com.example.chatwebproject.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user by username: " + username));
        return UserDetailsImpl.build(account);
    }
}
