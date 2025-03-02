package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.ERole;
import com.example.chatwebproject.model.Role;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.request.LoginRequest;
import com.example.chatwebproject.model.request.SignupRequest;
import com.example.chatwebproject.model.response.LoginResponse;
import com.example.chatwebproject.repository.RoleRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.security.jwt.JwtProvider;
import com.example.chatwebproject.security.service.UserDetailsImpl;
import com.example.chatwebproject.transformer.UserTransformer;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = this.jwtProvider.generateJwtToken(authentication);


        return ResponseEntity.ok(
                LoginResponse.builder()
                        .token(jwtToken)
                        .username(userDetails.getUsername())
                        .role(userDetails.getAuthorities().toArray()[0].toString())
                        .id(userDetails.getId())
                        .build());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest signupRequest) {
        Optional<User> optionalUser = this.userRepository.findByUsername(signupRequest.getUsername());
        if (optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username exists: " + signupRequest.getUsername());
        }

        User newUser = UserTransformer.transferToUser(signupRequest, this.passwordEncoder.encode(signupRequest.getPassword()));
        Optional<Role> roleOptional = this.roleRepository.findById(signupRequest.getRoleId());
        Role role;
        if (roleOptional.isEmpty()) {
            role = new Role();
            role.setId((long) ERole.ROLE_USER.getId());
            role.setRole(ERole.ROLE_USER);
        } else role = roleOptional.get();

        newUser.setRole(role);
        this.userRepository.save(newUser);

        return ResponseEntity.ok("Add user succeed");
    }


    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello Minh1");
    }

}
