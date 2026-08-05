package com.wdenberg.task.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.wdenberg.task.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {


    private static final String ISSUER = "todo-list-api";
    private static final Duration EXPIRATION_TIME = Duration.ofHours(2);


    private final String secret;


    public TokenService(
            @Value("${api.security.token.secret}") String secret
    ) {
        this.secret = secret;
    }



    public String generateToken(User user) {

        try {

            Algorithm algorithm = getAlgorithm();

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withExpiresAt(
                            generateExpirationToken()
                    )
                    .sign(algorithm);


        } catch (JWTCreationException exception) {

            throw new RuntimeException(
                    "Erro ao gerar token JWT",
                    exception
            );
        }
    }



    public String validateToken(String token) {

        try {

            return JWT.require(getAlgorithm())
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();


        } catch (JWTVerificationException exception) {

            return null;
        }
    }



    private Algorithm getAlgorithm() {

        return Algorithm.HMAC256(secret);
    }



    private Instant generateExpirationToken() {

        return Instant.now()
                .plus(EXPIRATION_TIME);
    }
}