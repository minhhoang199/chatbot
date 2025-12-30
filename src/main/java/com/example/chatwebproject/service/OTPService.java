package com.example.chatwebproject.service;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.infrastructure.EmailQueue;
import com.example.chatwebproject.model.entity.OTPVerification;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.enums.OTPType;
import com.example.chatwebproject.model.enums.UserStatus;
import com.example.chatwebproject.model.request.OTPSendRequest;
import com.example.chatwebproject.model.request.OTPGenerateRequest;
import com.example.chatwebproject.model.request.OTPVerifyRequest;
import com.example.chatwebproject.repository.OTPRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {
    private final long expiredMinutes = 5;
    private final SecureRandom rnd = new SecureRandom();
    private final OTPRepository otpRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public void generateAndSendOtp(OTPGenerateRequest request) {
        String email = request.getEmail();
        String OTPcode = getRandomCode();
        Instant expiredDate = DateUtil.localDateTimeToInstant(DateUtil.getCurrentDate()).plusSeconds(expiredMinutes * 60);
        //Save otp to DB
        OTPVerification newOTPVerification = OTPVerification.builder()
                .email(email)
                .expiredAt(expiredDate)
                .otpCode(OTPcode)
                .isVerified(false)
                .otpType(request.getOtpType())
                .newPassword(request.getNewPassword())
                .build();
        this.otpRepository.save(newOTPVerification);

        // 2. Push email task into blocking queue
        boolean offered = EmailQueue.QUEUE.offer(newOTPVerification);

        if (!offered) {
            // Queue full → fail fast or log
            throw new IllegalStateException("Email queue is full");
        }
    }

    private String getRandomCode() {
        Integer randomCode = (rnd.nextInt(9999) + 1000);
        if (randomCode > 9999) {
            randomCode -= 1000;
        }
        return String.valueOf(randomCode);
    }

    @Transactional
    public void verifyOTP(OTPVerifyRequest request) {
        List<OTPVerification> otpVerificationList = this.otpRepository.findByEmail(request.getEmail());
        if (CollectionUtils.isEmpty(otpVerificationList)) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found OTP by email!"}, null);
        }
        OTPVerification otpVerification = otpVerificationList.get(0);
        if (otpVerification.getOtpCode().equalsIgnoreCase(request.getCode())) {
            validateExpiredTimeOTP(otpVerification);
        } else {
            validateRetryTimes(otpVerification);
        }
        if (OTPType.SIGN_UP.equals(otpVerification.getOtpType())) {
            //active user
            this.userService.changeUserStatus(request.getEmail(), UserStatus.ACTIVE);
        } else {
            //change password
            this.userService.changePassword(request.getEmail(), otpVerification.getNewPassword());
        }
    }

    private void validateExpiredTimeOTP(OTPVerification otpVerification) {
        if (DateUtil.getCurrentDate().compareTo(DateUtil.instantToLocalDateTime(otpVerification.getExpiredAt())) > 0) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"OTP has been expired!"}, null);
        } else if (otpVerification.getIsVerified()) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"OTP has been verified!"}, null);
        } else if (otpVerification.getAttemptCounter() >= 5) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"OTP have been tried more than 5 times!"}, null);
        } else {
            //update OTP is verified
            otpVerification.setIsVerified(true);
            this.otpRepository.save(otpVerification);
        }
    }

    private void validateRetryTimes(OTPVerification otpVerification) {
        if (otpVerification.getAttemptCounter() < 5) {
            otpVerification.setAttemptCounter(otpVerification.getAttemptCounter() + 1);
            this.otpRepository.save(otpVerification);
        }
        throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Incorrect otp"}, null);
    }

    public void reSendOTP(OTPSendRequest otpSendRequest) {
        //validate user exist
        User user = this.userRepository.findByEmailAndDelFlgAndStatus(otpSendRequest.getEmail(), List.of(UserStatus.INACTIVE)).orElseThrow(
                () -> new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found inactive user"}, null)
        );
        // gen and send otp
        generateAndSendOtp(OTPGenerateRequest.builder().email(otpSendRequest.getEmail()).build());
    }
}
