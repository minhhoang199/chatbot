package com.example.chatwebproject.repository;

import com.example.chatwebproject.model.entity.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTPVerification, Long> {

    @Query("SELECT otp FROM OTPVerification otp " +
            " WHERE otp.email = :email " +
            " AND (otp.delFlag IS NULL OR otp.delFlag = false) " +
            " ORDER BY otp.createdAt DESC ")
    List<OTPVerification> findByEmail(String email);
}
