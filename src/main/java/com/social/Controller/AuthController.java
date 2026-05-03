package com.social.Controller;

import com.social.Service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =========================
    // 1️⃣ SEND OTP (REGISTRATION)
    // =========================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
            @RequestBody @Valid SendOtpRequest request
    ) {
		System.out.println("OTP endpoint HIT");
        authService.sendOtp(request.email());
        return ResponseEntity.ok(
                Map.of("message", "OTP sent successfully")
        );
    }

    // ==================================
    // 2️⃣ VERIFY OTP + REGISTER USER
    // ==================================
    @PostMapping("/verify-otp-register")
    public ResponseEntity<?> verifyOtpAndRegister(
            @RequestBody @Valid VerifyOtpRegisterRequest request
    ) {
    	System.out.println(">>> VERIFY OTP CONTROLLER HIT <<<");
        authService.verifyOtpAndRegister(
                request.email(),
                request.otp(),
                request.password(),
                request.name()
        );

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully")
        );
    }
	@PostMapping("/register")
	public ResponseEntity<?> register(
        @RequestBody @Valid RegisterRequest request
	) {
    	authService.register(
            request.email(),
            request.password(),
            request.name()
    	);

    	return ResponseEntity.ok(
            Map.of("message", "User registered successfully")
    	);
	}
    // =========================
    // 3️⃣ LOGIN (PASSWORD)
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request
    ) {
        String jwt = authService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(
                Map.of("token", jwt)
        );
    }

    // =========================
    // REQUEST DTOs (INNER)
    // =========================

    public record SendOtpRequest(
            @Email @NotBlank String email
    ) {}

    public record VerifyOtpRegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String otp,
            @NotBlank String password,
            @NotBlank String name
    ) {

		}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}
}
