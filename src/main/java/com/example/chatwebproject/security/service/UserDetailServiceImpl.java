package com.example.chatwebproject.security.service;

import com.example.chatwebproject.model.User;
import com.example.chatwebproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user by username: " + username));
        return UserDetailsImpl.build(user);
    }
}
