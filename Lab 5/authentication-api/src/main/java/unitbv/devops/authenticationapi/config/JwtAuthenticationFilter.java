package unitbv.devops.authenticationapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import unitbv.devops.authenticationapi.user.entity.Token;
import unitbv.devops.authenticationapi.user.repository.TokenJpaRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenJpaRepository tokenRepo;

    public JwtAuthenticationFilter(JwtService jwtService, TokenJpaRepository tokenRepo) {
        this.jwtService = jwtService;
        this.tokenRepo = tokenRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        // validate signature and expiry
        if (!jwtService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // check DB for token and blacklist flag
        java.util.Optional<Token> opt = tokenRepo.findTokenByAccessToken(token);
        if (opt.isEmpty()) {
            // unknown token -> reject (not authenticated)
            filterChain.doFilter(request, response);
            return;
        }

        Token dbToken = opt.get();
        if (dbToken.isBlacklistedAccess() || dbToken.isBlacklistedRefresh()) {
            filterChain.doFilter(request, response);
            return;
        }

        // create Authentication and set in SecurityContext
        var user = dbToken.getUser();
        List<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token).stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());

        var auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
