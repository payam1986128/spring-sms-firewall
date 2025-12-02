package ir.payam1986128.smsfirewall.virtualthread.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ir.payam1986128.smsfirewall.core.presentation.users.UserDetailsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtTokenService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.access-token-expiration}")
    private long accessTokenExpire;
    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpire;

    public String generateAccessToken(UserDetailsDto user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpire))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserDetailsDto user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpire))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isValidAccessToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && isTokenNotExpired(token));
    }

    public boolean isValidRefreshToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername)) && isTokenNotExpired(token);
    }

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenNotExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
