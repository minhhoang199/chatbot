package com.example.chatwebproject.service;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.enums.UserStatus;
import com.example.chatwebproject.model.request.ChangePasswordRequest;
import com.example.chatwebproject.model.request.EditUserInfoRequest;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.transformer.UserTransformer;
import com.example.chatwebproject.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public void save(User newUser) {
        if (newUser != null) {
            var userOtp = userRepository.findByEmailAndDelFlg(newUser.getEmail());
            if (userOtp.isPresent()) {
                throw new RuntimeException("Email already existed");
            }
            this.userRepository.save(newUser);
        }
    }

    public User getUserInfo(String email) {
        return this.userRepository.findByEmailAndDelFlg(email).orElseThrow(
                () -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found user by email: " + email})
        );
    }

    public List<UserDto> searchByEmail(String searchingEmail) {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<User> users = this.userRepository.searchByEmail(searchingEmail, currentEmail);
        if (CollectionUtils.isEmpty(users)) return new ArrayList<>();
        return UserTransformer.toDtoList(users);
    }


    public void updateUserInfo(EditUserInfoRequest request) {
        var userOtp = this.userRepository.findById(request.getId());
        if (userOtp.isEmpty()) {
            throw new RuntimeException("Not found user");
        }

        User currentUser = userOtp.get();

        String newUserName = request.getUsername();
        {
            if (newUserName.length() == 0) {
                throw new RuntimeException("Invalid username");
            }
            currentUser.setUsername(newUserName);
        }

//        String userVMEmail = request.getEmail();
//
//        var checkedUserOtp = userRepository.findByEmailAndDelFlg(userVMEmail);
//        if (checkedUserOtp.isPresent()) {
//            throw new RuntimeException("Email already existed");
//        }
//        currentUser.setEmail(userVMEmail);
        this.userRepository.save(currentUser);
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        String email = SecurityUtil.getCurrentEmailLogin();
        if (StringUtils.isEmpty(email) || !changePasswordRequest.getEmail().equals(email)) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Email is invalid: " + changePasswordRequest.getEmail()}, null);
        }
        if (changePasswordRequest.getNewPassword().length() < 8) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid password, at least 8 characters"});
        }
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(changePasswordRequest.getEmail(), changePasswordRequest.getOldPassword()));
        this.changePassword(changePasswordRequest.getEmail(), this.passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    }

    public void changePassword(String email, String newPassword) {
        User currentUser = this.getUserInfo(email);
        if (newPassword != null) {
            currentUser.setPassword(newPassword);
        }
        this.userRepository.save(currentUser);
    }

    public List<UserDto> getFriends() {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<User> userList = this.userRepository.getFriends(currentEmail);
        if (CollectionUtils.isEmpty(userList)) return new ArrayList<>();
        return UserTransformer.toDtoList(userList);
    }

    public void changeUserStatus(String email, UserStatus status) {
        User user = this.userRepository.findByEmailAndDelFlgAndStatus(email, List.of(UserStatus.INACTIVE))
                .orElseThrow(() -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found inactive user by email: " + email}));
        user.setStatus(status);
    }

    public List<UserDto> getRecentUserChat() {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<User> userList = this.userRepository.getRecentUserChat(currentEmail);
        if (CollectionUtils.isEmpty(userList)) return new ArrayList<>();
        return UserTransformer.toDtoList(userList);
    }
}
