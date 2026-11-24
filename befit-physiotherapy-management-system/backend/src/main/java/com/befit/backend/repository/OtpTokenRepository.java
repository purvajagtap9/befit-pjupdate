package com.befit.backend.repository;

import com.befit.backend.entity.OtpPurpose;
import com.befit.backend.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> 
{
    Optional<OtpToken> findByEmailAndPurpose(String email, OtpPurpose purpose);
}