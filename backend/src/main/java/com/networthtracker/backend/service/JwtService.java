package com.networthtracker.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.networthtracker.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

// creates and checks the short-lived access tokens (JWTs).
@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long accessTokenExpiration;

    // set up the signing algorithm + verifier from the secret in application.properties
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer("networth-tracker").build();
        this.accessTokenExpiration = accessTokenExpiration;
    }

    // build a signed token proving who the user is; expires in 15 min
    public String generateAccessToken(User user) {
        return JWT.create()
                .withIssuer("networth-tracker")
                .withSubject(String.valueOf(user.getId()))
                .withClaim("email", user.getEmail())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .sign(algorithm);
    }

    // check a token's signature and expiry; throws if it's invalid
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    // pull the user id out of a valid token (stored in the "subject" field)
    public Integer getUserIdFromToken(String token) {
        DecodedJWT decoded = validateToken(token);
        return Integer.parseInt(decoded.getSubject());
    }
}
