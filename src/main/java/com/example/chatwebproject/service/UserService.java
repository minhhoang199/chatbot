package com.example.chatwebproject.service;

import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User newUser) {
        if (newUser != null) {
            var userOtp = userRepository.findByEmail(newUser.getEmail());
            if (userOtp.isPresent()) {
                throw new RuntimeException("Email already existed");
            }
            this.userRepository.save(newUser);
        }
    }

    public User getUserInfo(Long userId) {
        if (userId == null ||
                userId <= 0) {
            throw new RuntimeException("Invalid user Id");
        }
        var userOtp = this.userRepository.findById(userId);
        if (userOtp.isEmpty()) {
            throw new RuntimeException("Not found user");
        }
        return userOtp.get();
    }


    public void updateUserInfo(Long userId, UserDto userVM) {
        if (userId == null ||
                userId <= 0) {
            throw new RuntimeException("Invalid user Id");
        }
        var userOtp = this.userRepository.findById(userId);
        if (userOtp.isEmpty()) {
            throw new RuntimeException("Not found user");
        }

        User currentUser = userOtp.get();

        String newUserName = userVM.getUsername();
        {
            if (newUserName.length() == 0) {
                throw new RuntimeException("Invalid username");
            }
            currentUser.setUsername(newUserName);
        }

        String userVMEmail = userVM.getEmail();

        var checkedUserOtp = userRepository.findByEmail(userVMEmail);
        if (checkedUserOtp.isPresent()) {
            throw new RuntimeException("Email already existed");
        }
        currentUser.setEmail(userVMEmail);
        this.userRepository.save(currentUser);
    }

    public void changePassword(Long userId, String newPassword) {
        if (userId == null ||
                userId <= 0) {
            throw new RuntimeException("Invalid user Id");
        }

        var userOtp = this.userRepository.findById(userId);
        if (userOtp.isEmpty()) {
            throw new RuntimeException("Not found user");
        }

        User currentUser = userOtp.get();

        if (newPassword != null) {
            if (newPassword.length() < 8) {
                throw new RuntimeException("Invalid password");
            }
            currentUser.setPassword(newPassword);
        }
        this.userRepository.save(currentUser);
    }
}
