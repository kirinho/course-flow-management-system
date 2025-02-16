package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.exceptions.CustomException;
import com.liushukov.courseFlow.models.EmailToken;
import com.liushukov.courseFlow.models.User;
import java.util.Optional;

public interface VerificationAccountService {

    Optional<EmailToken> getToken(String token);

    String createToken(User user);

    void sendVerificationEmail(User user) throws CustomException;

    User confirmEmail(String token) throws CustomException;
}
