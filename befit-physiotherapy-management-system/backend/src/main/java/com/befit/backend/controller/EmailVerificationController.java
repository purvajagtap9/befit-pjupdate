package com.befit.backend.controller;

import com.befit.backend.entity.OtpPurpose;
import com.befit.backend.exception.ApiException;
import com.befit.backend.repository.UserRepository;
import com.befit.backend.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class EmailVerificationController {
    private static final Map<String, String> GENERIC =
        Map.of("message", "If the email is registered, a code has been sent.");

    private final OtpService otpService;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public EmailVerificationController(OtpService otpService, UserRepository userRepo, PasswordEncoder encoder) {
        this.otpService = otpService; this.userRepo = userRepo; this.encoder = encoder;
    }

    private static String clean(Map<String, String> b) {
        String e = b.get("email");
        return e == null ? "" : e.trim();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> b) {
        String email = clean(b);
        if (!otpService.verify(email, OtpPurpose.VERIFY_EMAIL, b.get("otp")))
            throw ApiException.badRequest("Invalid or expired code.");
        userRepo.findByEmail(email).ifPresent(u -> { u.setEmailVerified(true); userRepo.save(u); });
        return ResponseEntity.ok(Map.of("message", "Email verified."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resend(@RequestBody Map<String, String> b) {
        String email = clean(b);
        userRepo.findByEmail(email).filter(u -> !u.isEmailVerified())
                .ifPresent(u -> otpService.send(email, OtpPurpose.VERIFY_EMAIL));
        return ResponseEntity.ok(GENERIC);   // email ahe ki nahi te kalat nahi
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody Map<String, String> b) {
        String email = clean(b);
        userRepo.findByEmail(email).filter(u -> u.isActive())
                .ifPresent(u -> otpService.send(email, OtpPurpose.RESET_PASSWORD));
        return ResponseEntity.ok(GENERIC);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody Map<String, String> b) {
        String email = clean(b);
        String pw = b.get("newPassword");
        if (pw == null || pw.length() < 8 || pw.length() > 100)
            throw ApiException.badRequest("Password must be at least 8 characters.");
        if (!otpService.verify(email, OtpPurpose.RESET_PASSWORD, b.get("otp")))
            throw ApiException.badRequest("Invalid or expired code.");

        var user = userRepo.findByEmail(email).filter(u -> u.isActive())
                .orElseThrow(() -> ApiException.badRequest("Invalid or expired code."));
        user.setPassword(encoder.encode(pw));
        user.setEmailVerified(true);   // OTP ne email control sidhha kela
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated."));
    }
}