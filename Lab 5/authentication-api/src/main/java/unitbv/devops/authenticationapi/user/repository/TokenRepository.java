package unitbv.devops.authenticationapi.user.repository;

import java.util.Optional;

import unitbv.devops.authenticationapi.user.entity.Token;

public interface TokenRepository{
    Optional<Token> findTokenByAccessToken(String accessToken);

    Optional<Token> findTokenByRefreshToken(String refreshToken);
}