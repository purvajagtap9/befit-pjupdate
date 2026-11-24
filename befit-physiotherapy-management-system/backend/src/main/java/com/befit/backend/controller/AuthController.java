package com.befit.backend.controller;

import com.befit.backend.dto.AuthResponse;
import com.befit.backend.dto.LoginRequest;
import com.befit.backend.dto.RegisterRequest;
import com.befit.backend.entity.User;
import com.befit.backend.service.OtpService;
import com.befit.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.befit.backend.entity.OtpPurpose;
import com.befit.backend.service.OtpService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController 
{

    private final UserService userService;
    private final OtpService otpService;

    public AuthController(UserService userService, OtpService otpService) 
    {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody RegisterRequest request) 
    {
        User registeredUser = userService.registerPatient(request);
        otpService.send(registeredUser.getEmail(), OtpPurpose.VERIFY_EMAIL);   
        return ResponseEntity.ok(Map.of("message", "Patient registered successfully!",
        "userId", registeredUser.getId(), "emailVerificationRequired", true));
    }
    

    /** Success body is unchanged: { token, role, fullName, branchId }. Failures are 401 { code, message }. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }
}
