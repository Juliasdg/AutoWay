package br.com.fatec.autoway.infra.repository.jpa;

import br.com.fatec.autoway.domain.model.PasswordResetToken;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PasswordResetTokenRepository {

    private final Map<String, PasswordResetToken> storage = new ConcurrentHashMap<>();

    public void save(PasswordResetToken token) {
        storage.put(token.getToken(), token);
    }

    public Optional<PasswordResetToken> findByUserIdAndToken(String userId, String token) {
        PasswordResetToken t = storage.get(token);
        if (t != null && t.getUserId().equals(userId)) {
            return Optional.of(t);
        }
        return Optional.empty();
    }

    public void delete(PasswordResetToken token) {
        storage.remove(token.getToken());
    }

    public List<PasswordResetToken> findAll() {
        return new ArrayList<>(storage.values());
    }
}
