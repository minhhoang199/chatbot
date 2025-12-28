package com.example.chatwebproject.transformer;


import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.entity.ERole;
import com.example.chatwebproject.model.entity.Role;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.request.SignupRequest;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UserTransformer {
    public static User transferToUser(SignupRequest signupRequest, String encodePassword){
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encodePassword);
        return user;
    }

    private static Role buildCustomerRole(long roleId) {
        Role role = new Role();
        role.setId((long) roleId);
        role.setRole(roleId == 2 ? ERole.ROLE_ADMIN : ERole.ROLE_USER);
        return role;
    }

    public static UserDto toDto(User entity){
        if (ObjectUtils.isEmpty(entity)) return null;
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(UserTransformer::toDto).collect(Collectors.toList());
    }

//    private static Role builAdminRole() {
//        Role role = new Role();
//        role.setId((long) ERole.ROLE_ADMIN.id);
//        role.setRole(ERole.ROLE_ADMIN);
//        return role;
//    }
}
