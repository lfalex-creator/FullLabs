package unitbv.devops.authenticationapi.user.service;

import unitbv.devops.authenticationapi.dto.auth.GetRequest;
import unitbv.devops.authenticationapi.dto.auth.LoginRequest;
import unitbv.devops.authenticationapi.dto.auth.RegisterRequest;
import unitbv.devops.authenticationapi.dto.auth.TokenRequest;
import unitbv.devops.authenticationapi.dto.auth.UserResponse;
import unitbv.devops.authenticationapi.user.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    public Optional<UserResponse> register(RegisterRequest req);
    public Optional<UserResponse> login(LoginRequest req);
    public List<User> get(GetRequest req);
    public boolean blacklistToken(TokenRequest token);
    public Optional<UserResponse> refresh(TokenRequest token);
}
