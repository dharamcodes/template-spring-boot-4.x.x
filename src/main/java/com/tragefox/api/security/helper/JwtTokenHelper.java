package com.tragefox.api.security.helper;

import com.tragefox.api.exception.ApplicationException;
import com.tragefox.api.exception.constant.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class JwtTokenHelper {

    private final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode("UVVETsBqGWkYVZrM+VWTEMPn/aHp+HLjJL8hQlFyytQ="));
    private final Instant EXPIRY_MINUTE = Instant.now().plus(7200, ChronoUnit.MINUTES);
    private final Instant EXPIRY_MINUTE_RESET_PASSWORD = Instant.now().plus(15, ChronoUnit.MINUTES);

    public String generateToken(String userName, Map<String, String> claims) {
        return Jwts.builder()
                .subject(userName)
                .issuedAt(Date.from(Instant.now()))
                .claims(claims)
                .expiration(Date.from(EXPIRY_MINUTE))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateResetPasswordToken(String userName, Map<String, String> claims) {
        return Jwts.builder()
                .subject(userName)
                .issuedAt(Date.from(Instant.now()))
                .claims(claims)
                .expiration(Date.from(EXPIRY_MINUTE_RESET_PASSWORD))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Claims getTokenBody(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new ApplicationException(ErrorType.DATA_NOT_FOUND);
        }
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}
