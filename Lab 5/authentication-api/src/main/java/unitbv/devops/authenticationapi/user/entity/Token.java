package unitbv.devops.authenticationapi.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "access_token", nullable = true, columnDefinition = "TEXT")
    private String accessToken; // JWT

    @Column(name = "access_expiration_date", nullable = true)
    private Instant accessExpirationDate;

    @Column(name = "refresh_token", nullable = true, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "refresh_expiration_date", nullable = true)
    private Instant refreshExpirationDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "blacklisted_access", nullable = false)
    private boolean blacklistedAccess;

    @Column(name = "blacklisted_refresh", nullable = false)
    private boolean blacklistedRefresh;
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        // default not blacklisted (primitive boolean defaults to false)
    }

    // Explicit getters/setters for blacklisted fields (avoid Lombok ambiguity)
    public boolean isBlacklistedAccess() {
        return this.blacklistedAccess;
    }

    public void setBlacklistedAccess(boolean v) {
        this.blacklistedAccess = v;
    }

    public boolean isBlacklistedRefresh() {
        return this.blacklistedRefresh;
    }

    public void setBlacklistedRefresh(boolean v) {
        this.blacklistedRefresh = v;
    }
}
