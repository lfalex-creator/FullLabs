package unitbv.devops.authenticationapi.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unitbv.devops.authenticationapi.dto.auth.GetRequest;
import unitbv.devops.authenticationapi.dto.auth.GetResponse;
import unitbv.devops.authenticationapi.dto.auth.LoginRequest;
import unitbv.devops.authenticationapi.dto.auth.LoginResponse;
import unitbv.devops.authenticationapi.dto.auth.RegisterRequest;
import unitbv.devops.authenticationapi.dto.auth.TokenRequest;
import unitbv.devops.authenticationapi.user.service.UserService;

import unitbv.devops.authenticationapi.user.entity.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return service.register(request)
                .<ResponseEntity<?>>map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new SimpleError("Username or email already in use")));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return service.login(request)
                .map(user -> ResponseEntity.ok(new LoginResponse(true, user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, null)));
    }

    public String toString(List<User> users)
    {
        StringBuilder sb=new StringBuilder();
        for(User user:users)
        {
            sb.append(user.toString());
        }
        return sb.toString();
    }

    @GetMapping("/get")
    public ResponseEntity<GetResponse> get()
    {
        String aux;
        GetRequest request = new GetRequest();
        List<User> users = service.get(request);
        aux = toString(users);
        return ResponseEntity.ok(new GetResponse(aux));
    }

    @PostMapping("/token")
    public ResponseEntity<SimpleError> token(@RequestBody TokenRequest token)
    {
        var result=service.refresh(token);
        if(result.isPresent())  {
            return ResponseEntity.ok(new SimpleError("Token refreshed successfully"));
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleError("Invalid or expired token"));
        }
    }
    public record SimpleError(String error) {}
}
