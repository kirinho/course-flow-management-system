package com.liushukov.courseFlow.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushukov.courseFlow.dtos.EmailDto;
import com.liushukov.courseFlow.exceptions.CustomException;
import com.liushukov.courseFlow.models.EmailToken;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.EmailTokenRepository;
import com.liushukov.courseFlow.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationAccountService {

    @Value("${security.authenticate.user.account.url}")
    private String url;

    @Value("${kafka.verification.topic}")
    private String verificationTopicName;

    private final EmailTokenRepository emailTokenRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<Object, Object> template;
    private final ObjectMapper objectMapper;

    public VerificationAccountService(EmailTokenRepository emailTokenRepository, UserRepository userRepository, KafkaTemplate<Object, Object> template, ObjectMapper objectMapper) {
        this.emailTokenRepository = emailTokenRepository;
        this.userRepository = userRepository;
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public Optional<EmailToken> getToken(String token){
        return emailTokenRepository.findByToken(token);
    }

    public String createToken(User user){
        Optional<EmailToken> existingToken = emailTokenRepository.findByUserId(user.getId());
        existingToken.ifPresent(emailTokenRepository::delete);
        String token = UUID.randomUUID().toString();
        var emailToken = new EmailToken(
                token,
                Instant.now().plus(24, ChronoUnit.HOURS),
                user
        );
        emailTokenRepository.save(emailToken);
        return token;
    }

    public String sendVerificationEmail(User user) throws CustomException {
        try {
            var token = createToken(user);
            String verificationUrl = String.format("%s?token=%s", url, token);
            String message = buildEmail(user.getName(), user.getSurname(), verificationUrl);
            EmailDto emailDto = new EmailDto(user.getEmail(), message);
            this.template.send(verificationTopicName, objectMapper.writeValueAsString(emailDto));
            return "View your email, we've just sent verification letter";
        } catch (Exception exception) {
            throw new CustomException("Some error occurred, please try again", HttpStatus.BAD_REQUEST);
        }
    }

    public User confirmEmail(String token) throws CustomException {
        Optional<EmailToken> confirmToken = getToken(token);
        if (confirmToken.isEmpty()){
            throw new CustomException("Token doesn't exist!", HttpStatus.BAD_REQUEST);
        }
        if (confirmToken.get().isActivated()){
            throw new CustomException("Token was already activated", HttpStatus.CONFLICT);
        }
        if (confirmToken.get().getExpirationTime().isBefore(Instant.now())){
            throw new CustomException("Token is expired", HttpStatus.NOT_FOUND);
        }
        User user = confirmToken.get().getUser();
        user.setEnabled(true);
        userRepository.save(user);
        var updateToken = confirmToken.get();
        updateToken.setActivated(true);
        emailTokenRepository.save(updateToken);
        return user;
    }

    private String buildEmail(String name, String surname, String token){
        return "<div style='font-family: Arial, sans-serif; font-size: 16px; color: #333;'>"
                + "<h2 style='color: #0066cc;'>Welcome to Our App, " + name + " " + surname + "!</h2>"
                + "<p>Thank you for registering. Please click the link below to verify your account:</p>"
                + "<p><a href='" + token + "' style='color: #28a745; text-decoration: none;'>"
                + "<b>Click here to verify your account</b></a></p>"
                + "<p>If you have any issues, please contact our support team.</p>"
                + "<p>Best regards,<br>Your Accounting :)</p>"
                + "</div>";
    }
}
