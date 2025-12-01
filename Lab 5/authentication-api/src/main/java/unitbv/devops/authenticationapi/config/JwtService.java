package unitbv.devops.authenticationapi.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import unitbv.devops.authenticationapi.user.entity.User;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Key signingKey;
    private final long accessTokenValiditySeconds;

    public JwtService(@Value("${jwt.secret:ReplaceThisWithAStrongSecretKeyAtLeast32Bytes}") String secret,
                      @Value("${jwt.access-expiration-seconds:3600}") long accessTokenValiditySeconds) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenValiditySeconds);
        return Jwts.builder()
                .setSubject(user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    // parse claims and basic validation
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims c = parseClaims(token);
            Date exp = c.getExpiration();
            return exp != null && exp.after(Date.from(Instant.now()));
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public java.util.List<String> extractRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        if (roles instanceof java.util.List<?>) {
            return ((java.util.List<?>) roles).stream().map(Object::toString).toList();
        }
        return java.util.List.of();
    }
}
