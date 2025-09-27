package br.com.fatec.autoway.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class PasswordResetToken {
    private String token;       // UUID ou código
    private String userId;      // ID do usuário
    private LocalDateTime expiresAt;
    private boolean used;

    public PasswordResetToken(String token, String userId, LocalDateTime expiresAt, boolean used) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}
