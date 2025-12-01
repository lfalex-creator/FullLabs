package unitbv.devops.authenticationapi.user.repository.impl;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import unitbv.devops.authenticationapi.user.entity.Token;
import unitbv.devops.authenticationapi.user.repository.TokenJpaRepository;
import unitbv.devops.authenticationapi.user.repository.TokenRepository;

@Repository
@Primary
@RequiredArgsConstructor
public class TokenRepositoryDb implements TokenRepository{

    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Optional<Token> findTokenByAccessToken(String accessToken){
        return tokenJpaRepository.findTokenByAccessToken(accessToken);
    }
    @Override
    public Optional<Token> findTokenByRefreshToken(String refreshToken){
        return tokenJpaRepository.findTokenByRefreshToken(refreshToken);
    }
}