package br.com.fatec.autoway.infra.repository;

import br.com.fatec.autoway.domain.model.ConfirmationToken;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ConfirmationTokenRepository {
    private final Map<String, ConfirmationToken> storage = new ConcurrentHashMap<>();

    public void save(ConfirmationToken token) {
        storage.put(token.getToken(), token);
    }

    public Optional<ConfirmationToken> findByToken(String token) {
        return Optional.ofNullable(storage.get(token));
    }
}
