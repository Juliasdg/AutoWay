package br.com.fatec.autoway.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ConfirmationToken {
    private String token;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean confirmed;

    public ConfirmationToken(String token, String userId, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmed = false;
    }
}
