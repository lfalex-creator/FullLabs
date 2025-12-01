package unitbv.devops.authenticationapi.user.mapper;

import unitbv.devops.authenticationapi.dto.auth.UserResponse;
import unitbv.devops.authenticationapi.user.entity.Role;
import unitbv.devops.authenticationapi.user.entity.User;
import unitbv.devops.authenticationapi.user.entity.Token;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {
    private UserMapper() {}

    public static UserResponse toResponse(User u) {
        Set<String> roles = u.getRoles() == null ? Set.of()
                : u.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        String accessToken = null;
        String refreshToken = null;
        if (u.getTokens() != null && !u.getTokens().isEmpty()) {
            Token t = u.getTokens().iterator().next();
            accessToken = t.getAccessToken();
            refreshToken = t.getRefreshToken();
        }

        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                roles,
                u.getCreatedAt(),
                u.isEnabled(),
            accessToken,
            refreshToken
        );
    }

        public static UserResponse toResponse(User u, Token t) {
        Set<String> roles = u.getRoles() == null ? Set.of()
            : u.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        String accessToken = t == null ? null : t.getAccessToken();
        String refreshToken = t == null ? null : t.getRefreshToken();
        return new UserResponse(
            u.getId(),
            u.getUsername(),
            u.getEmail(),
            roles,
            u.getCreatedAt(),
            u.isEnabled(),
            accessToken,
            refreshToken
        );
        }
}
