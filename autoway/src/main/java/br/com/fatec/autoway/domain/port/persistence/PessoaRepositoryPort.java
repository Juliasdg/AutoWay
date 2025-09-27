package br.com.fatec.autoway.domain.port.persistence;

import br.com.fatec.autoway.domain.model.Pessoa;

import java.util.List;
import java.util.Optional;

public interface PessoaRepositoryPort {
    Pessoa save(Pessoa pessoa);
    Optional<Pessoa> findById(String id);
    Optional<Pessoa> findByEmail(String email);
    Optional<Pessoa> findByNome(String nome);
    Optional<Pessoa> findByCpf(String cpf);
    List<Pessoa> findAll();
    void updateStatus(String id, Boolean status);
}
