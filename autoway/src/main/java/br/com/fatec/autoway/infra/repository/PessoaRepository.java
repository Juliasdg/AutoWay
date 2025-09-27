package br.com.fatec.autoway.infra.repository;

import br.com.fatec.autoway.infra.orm.PessoaOrm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<PessoaOrm, String> {
    Optional<PessoaOrm> findByEmail(String email);
    Optional<PessoaOrm> findByNome(String nome);
    Optional<PessoaOrm> findByCpf(String cpf);
}