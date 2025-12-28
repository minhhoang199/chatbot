package com.example.chatwebproject.controller;


import com.example.chatwebproject.constant.Constants;
import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.entity.ERole;
import com.example.chatwebproject.model.entity.Role;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.enums.UserStatus;
import com.example.chatwebproject.model.request.LoginRequest;
import com.example.chatwebproject.model.request.LogoutRequest;
import com.example.chatwebproject.model.request.OTPSendRequest;
import com.example.chatwebproject.model.request.OTPGenerateRequest;
import com.example.chatwebproject.model.request.OTPVerifyRequest;
import com.example.chatwebproject.model.request.SignupRequest;
import com.example.chatwebproject.model.response.LoginResponse;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.repository.RoleRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.security.jwt.JwtProvider;
import com.example.chatwebproject.security.service.UserDetailImpl;
import com.example.chatwebproject.service.OTPService;
import com.example.chatwebproject.transformer.UserTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/security/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://192.168.1.115:4200")
//@CrossOrigin(origins = "https://192.168.1.115:4200")
public class AuthController {
    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    public static final String DIGITS = "0123456789";
    public static final String COMBINATION_STRING = UPPER + LOWER + DIGITS;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;
    private final RespFactory respFactory;
    private final OTPService otpService;
//    private RedisUtil redisUtil;

//    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> signIn(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        String jwtToken = this.jwtProvider.generateJwtToken(authentication);

        //Set sessionId vào redis
//        String key = "email:" + userDetails.getEmail();
//        this.redisUtil.setValue(key, sessionId);

        return this.respFactory.success(LoginResponse.builder()
                .token(jwtToken)
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(userDetails.getAuthorities().toArray()[0].toString())
                .id(userDetails.getId())
                .build());
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> signUp(@RequestBody @Valid SignupRequest signupRequest) {
        Optional<User> optionalUser = this.userRepository.findByEmailAndDelFlgAndStatus(signupRequest.getEmail(), List.of(UserStatus.ACTIVE, UserStatus.INACTIVE));
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            if (currentUser.getStatus().equals(UserStatus.ACTIVE)) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Email exists: " + signupRequest.getEmail()}, null);
            } else {
                //INACTIVE delete old record
                currentUser.setDelFlag(true);
                currentUser.setStatus(UserStatus.DROPPED);
            }
        }

        User newUser = UserTransformer.transferToUser(signupRequest, this.passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setStatus(UserStatus.INACTIVE);
        newUser.setDelFlag(false);
        Optional<Role> roleOptional = this.roleRepository.findById(signupRequest.getRoleId());
        Role role;
        if (roleOptional.isEmpty()) {
            role = new Role();
            role.setId((long) ERole.ROLE_USER.getId());
            role.setRole(ERole.ROLE_USER);
        } else role = roleOptional.get();

        newUser.setRole(role);
        this.userRepository.save(newUser);
        this.otpService.generateAndSendOtp(OTPGenerateRequest.builder().email(newUser.getEmail()).build());

        return ResponseEntity.ok("Add user succeed");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest logoutRequest,
                                    @RequestHeader(Constants.AUTHORIZATION_HEADER) String token) {
        //check email trong token
        if (!this.jwtProvider.checkEmailInToken(logoutRequest.getEmail(), token)) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid email"}, null);
        }
        Optional<User> optionalUser = this.userRepository.findByEmailAndDelFlg(logoutRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found user by email"}, null);
        }

        //xoá trong Redis
//        String key = "email:" + logoutRequest.getEmail();
//        this.redisUtil.deleteValue(key);
        return this.respFactory.success();
    }

    @PutMapping("/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestBody OTPVerifyRequest otpVerifyRequest) {
        this.otpService.verifyOTP(otpVerifyRequest);
        return this.respFactory.success();
    }

    @PostMapping("/re-send-OTP")
    public ResponseEntity<?> reSendOTP(@RequestBody OTPSendRequest otpSendRequest) {
        this.otpService.reSendOTP(otpSendRequest);
        return this.respFactory.success();
    }
}
