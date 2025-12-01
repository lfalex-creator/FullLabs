package unitbv.devops.authenticationapi.user.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import unitbv.devops.authenticationapi.config.JwtService;
import unitbv.devops.authenticationapi.dto.auth.GetRequest;
import unitbv.devops.authenticationapi.dto.auth.LoginRequest;
import unitbv.devops.authenticationapi.dto.auth.RegisterRequest;
import unitbv.devops.authenticationapi.dto.auth.TokenRequest;
import unitbv.devops.authenticationapi.dto.auth.UserResponse;
import unitbv.devops.authenticationapi.user.entity.Role;
import unitbv.devops.authenticationapi.user.entity.Token;
import unitbv.devops.authenticationapi.user.entity.User;
import unitbv.devops.authenticationapi.user.mapper.UserMapper;
import unitbv.devops.authenticationapi.user.repository.TokenJpaRepository;
import unitbv.devops.authenticationapi.user.repository.UserRepository;
import unitbv.devops.authenticationapi.user.service.UserService;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TokenJpaRepository tokens;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int REFRESH_TOKEN_BYTES = 32;
    private static final long REFRESH_TOKEN_EXPIRY_SECONDS = 60L * 60L * 24L * 30L; // 30 days

    public UserServiceImpl(UserRepository users, PasswordEncoder encoder, JwtService jwtService, TokenJpaRepository tokens) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.tokens = tokens;
    }

    @Override
    public Optional<UserResponse> register(RegisterRequest req) {
        if (users.existsByUsername(req.username()) || users.existsByEmail(req.email())) {
            return Optional.empty();
        }

        User u = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .roles(new HashSet<>(Set.of(Role.USER)))
                .createdAt(Instant.now())
                .enabled(true)
                .build();

        u = users.save(u);
        return Optional.of(UserMapper.toResponse(u));
    }

    @Override
    public Optional<UserResponse> login(LoginRequest req) {
        Optional<User> found = users.findByUsername(req.usernameOrEmail());
        if (found.isEmpty()) {
            found = users.findByEmail(req.usernameOrEmail());
        }

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User u = found.get();
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            return Optional.empty();
        }

        // generate access token (JWT) and refresh token (opaque random) and persist
        String accessToken = jwtService.generateAccessToken(u);
        String refreshToken = generateRandomToken(REFRESH_TOKEN_BYTES);

        Instant now = Instant.now();
        Token t = Token.builder()
                .user(u)
                .accessToken(accessToken)
                .accessExpirationDate(now.plusSeconds(jwtService.getAccessTokenValiditySeconds()))
                .refreshToken(refreshToken)
                .refreshExpirationDate(now.plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS))
                .build();

        // attach token to user and save (cascade will persist token)
        u.getTokens().add(t);
        u = users.save(u);

        return Optional.of(UserMapper.toResponse(u, t));
    }
    @Override
    public List<User> get(GetRequest req)
    {
        return users.findAll();
    }

    @Override
    public Optional<UserResponse> refresh(TokenRequest tokenReq) {
        Optional<Token> optional = tokens.findTokenByRefreshToken(tokenReq.refreshToken());
        if (optional.isEmpty()) return Optional.empty();

        Token t = optional.get();
        // check blacklisted flags
        if (t.isBlacklistedAccess() || t.isBlacklistedRefresh()) return Optional.empty();

        Instant now = Instant.now();
        if (t.getRefreshExpirationDate() != null && t.getRefreshExpirationDate().isBefore(now)) {
            return Optional.empty();
        }

        User u = t.getUser();

        t.setBlacklistedAccess(true);
        t.setBlacklistedRefresh(true);
        tokens.save(t);

        // issue new tokens and persist as a NEW Token row
        String newAccess = jwtService.generateAccessToken(u);
        String newRefresh = generateRandomToken(REFRESH_TOKEN_BYTES);

        Token newToken = Token.builder()
            .user(u)
            .accessToken(newAccess)
            .accessExpirationDate(now.plusSeconds(jwtService.getAccessTokenValiditySeconds()))
            .refreshToken(newRefresh)
            .refreshExpirationDate(now.plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS))
            .createdAt(now)
            .blacklistedAccess(false)
            .blacklistedRefresh(false)
            .build();

        tokens.save(newToken);

        return Optional.of(UserMapper.toResponse(u, newToken));
    }

    private static String generateRandomToken(int bytes) {
        byte[] b = new byte[bytes];
        RANDOM.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    
    

    @Override
    public boolean blacklistToken(TokenRequest token) {
        boolean ok=false;
        Optional<Token> optionalToken = tokens.findTokenByAccessToken(token.accessToken());
        if (optionalToken.isPresent()) {
            Token t = optionalToken.get();
            t.setBlacklistedAccess(true);
            tokens.save(t);
            ok=true;
        }
        optionalToken = tokens.findTokenByRefreshToken(token.refreshToken());
        if (optionalToken.isPresent()) {
            Token t = optionalToken.get();
            t.setBlacklistedRefresh(true);
            tokens.save(t);
            ok=true;
        }
        else
        {
            ok=false;
        }
        if(ok)
            return true;
        return false;
    }
}