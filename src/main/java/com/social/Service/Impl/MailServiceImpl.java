package com.social.Service.Impl;

import com.social.Service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("XSocial - OTP Verification");
        message.setText(
                "Your OTP for XSocial registration is: " + otp +
                "\n\nThis OTP is valid for 5 minutes."
        );

        mailSender.send(message);
    }
}
