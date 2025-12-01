package unitbv.devops.authenticationapi.user.repository;

import unitbv.devops.authenticationapi.user.entity.Token;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<Token, String>{
    Optional<Token> findTokenByAccessToken(String accessToken);

    Optional<Token> findTokenByRefreshToken(String refreshToken);
}
