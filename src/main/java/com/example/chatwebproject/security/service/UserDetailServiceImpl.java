package com.example.chatwebproject.security.service;


import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmailAndDelFlg(email)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService :: loadUserByUsername: email not exist: " + email));

        return UserDetailImpl.build(user);
    }
}
