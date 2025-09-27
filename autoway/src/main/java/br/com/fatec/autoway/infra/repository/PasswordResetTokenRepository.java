package br.com.fatec.autoway.infra.repository;

import br.com.fatec.autoway.domain.model.PasswordResetToken;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PasswordResetTokenRepository {

    // Armazenamento por token
    private final Map<String, PasswordResetToken> storage = new ConcurrentHashMap<>();

    // Salva ou atualiza o token
    public void save(PasswordResetToken token) {
        storage.put(token.getToken(), token);
    }

    // Busca token pelo userId e token
    public Optional<PasswordResetToken> findByUserIdAndToken(String userId, String token) {
        PasswordResetToken t = storage.get(token);
        if (t != null && t.getUserId().equals(userId)) {
            return Optional.of(t);
        }
        return Optional.empty();
    }

    // Remove token após uso
    public void delete(PasswordResetToken token) {
        storage.remove(token.getToken());
    }

    // Retorna todos tokens (apenas para debug)
    public List<PasswordResetToken> findAll() {
        return new ArrayList<>(storage.values());
    }
}
