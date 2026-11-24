package com.befit.backend.service;

import com.befit.backend.entity.OtpPurpose;
import com.befit.backend.entity.OtpToken;
import com.befit.backend.repository.OtpTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private final OtpTokenRepository repo;
    private final PasswordEncoder encoder;
    private final EmailSender mail;
    private final long expiryMin, cooldownSec;
    private final int maxAttempts;

    public OtpService(OtpTokenRepository repo, PasswordEncoder encoder, EmailSender mail,
                      @Value("${otp.expiry-minutes}") long expiryMin,
                      @Value("${otp.cooldown-seconds}") long cooldownSec,
                      @Value("${otp.max-attempts}") int maxAttempts) {
        this.repo = repo; this.encoder = encoder; this.mail = mail;
        this.expiryMin = expiryMin; this.cooldownSec = cooldownSec; this.maxAttempts = maxAttempts;
    }

    /** false = cooldown madhe ahe, email pathvla nahi */
    public boolean send(String email, OtpPurpose purpose) {
        LocalDateTime now = LocalDateTime.now();
        OtpToken t = repo.findByEmailAndPurpose(email, purpose).orElse(null);
        if (t != null && t.getLastSentAt().plusSeconds(cooldownSec).isAfter(now)) return false;
        if (t == null) t = new OtpToken(email, purpose);

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        t.setOtpHash(encoder.encode(otp));          // junya OTP la overwrite karto (invalidate)
        t.setExpiry(now.plusMinutes(expiryMin));
        t.setLastSentAt(now);
        t.setAttempts(0);
        repo.save(t);

        String subject = purpose == OtpPurpose.VERIFY_EMAIL ? "BeFit - Verify your email" : "BeFit - Reset your password";
        try {
            mail.send(email, subject, "Your BeFit code is " + otp + ". It is valid for " + expiryMin + " minutes.");
        } catch (Exception e) {
            log.warn("Could not send {} email: {}", purpose, e.getClass().getSimpleName()); // OTP kadhich log karat nahi
        }
        return true;
    }

    public boolean verify(String email, OtpPurpose purpose, String otp) {
        OtpToken t = repo.findByEmailAndPurpose(email, purpose).orElse(null);
        if (t == null || otp == null || t.getExpiry().isBefore(LocalDateTime.now())) return false;
        if (t.getAttempts() >= maxAttempts) { repo.delete(t); return false; }
        if (!encoder.matches(otp.trim(), t.getOtpHash())) {
            t.setAttempts(t.getAttempts() + 1);
            repo.save(t);
            return false;
        }
        repo.delete(t);   // ek vela vaparaycha
        return true;
    }
}