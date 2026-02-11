package com.social.Service.Impl;

import com.social.Model.OtpVerification;
import com.social.Model.User;
import com.social.Model.Enums.Role;
import com.social.Repository.OtpVerificationRepository;
import com.social.Repository.UserRepository;
import com.social.Service.AuthService;
import com.social.Util.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.social.Service.MailService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OtpVerificationRepository otpRepo;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    
    @Transactional
    @Override
    public void sendOtp(String email) {

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Remove old OTP (if any)
        otpRepo.deleteByEmail(email);

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepo.save(verification);

        // 🔴 THIS WAS MISSING
        mailService.sendOtpEmail(email, otp);
    }

    @Transactional
    @Override
    public void verifyOtpAndRegister(String email, String otp, String rawPassword,String name) {

        OtpVerification verification = otpRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        if (!verification.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        if (userRepository.existsByEmail(email))
            throw new RuntimeException("User already exists");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        otpRepo.deleteByEmail(email);
    }

    @Override
    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        return jwtService.generateToken(user);
    }
}
