package com.example.chatwebproject.transformer;


import com.example.chatwebproject.dto.request.SignupRequest;
import com.example.chatwebproject.model.ERole;
import com.example.chatwebproject.model.Role;
import com.example.chatwebproject.model.User;

public class UserTransformer {
    public static User transferToUser(SignupRequest signupRequest, String encodePassword){
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPhone(signupRequest.getPhone());
        user.setPassword(encodePassword);
        return user;
    }

    private static Role buildCustomerRole(long roleId) {
        Role role = new Role();
        role.setId((long) roleId);
        role.setRole(roleId == 2 ? ERole.ROLE_ADMIN : ERole.ROLE_USER);
        return role;
    }

//    private static Role builAdminRole() {
//        Role role = new Role();
//        role.setId((long) ERole.ROLE_ADMIN.id);
//        role.setRole(ERole.ROLE_ADMIN);
//        return role;
//    }
}
