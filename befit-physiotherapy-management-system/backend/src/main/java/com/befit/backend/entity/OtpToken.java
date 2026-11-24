package com.befit.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_token", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "purpose"}))
public class OtpToken 
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String email;
    @Enumerated(EnumType.STRING) private OtpPurpose purpose;
    private String otpHash;
    private LocalDateTime expiry;
    private LocalDateTime lastSentAt;
    private int attempts;

    public OtpToken() {}
    public OtpToken(String email, OtpPurpose purpose) { this.email = email; this.purpose = purpose; }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public OtpPurpose getPurpose() { return purpose; }
    public String getOtpHash() { return otpHash; } public void setOtpHash(String v) { this.otpHash = v; }
    public LocalDateTime getExpiry() { return expiry; } public void setExpiry(LocalDateTime v) { this.expiry = v; }
    public LocalDateTime getLastSentAt() { return lastSentAt; } public void setLastSentAt(LocalDateTime v) { this.lastSentAt = v; }
    public int getAttempts() { return attempts; } public void setAttempts(int v) { this.attempts = v; }
}