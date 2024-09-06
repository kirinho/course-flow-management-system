package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByToken(String token);

    Optional<EmailToken> findByUserId(Long userId);
}

