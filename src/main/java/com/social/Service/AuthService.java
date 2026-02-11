package com.social.Service;

public interface AuthService {

    void sendOtp(String email);

    void verifyOtpAndRegister(String email, String otp, String rawPassword,String name);

    String login(String email, String password);
}
