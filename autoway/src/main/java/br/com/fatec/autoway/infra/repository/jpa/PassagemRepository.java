package br.com.fatec.autoway.infra.repository.jpa;

import br.com.fatec.autoway.infra.orm.PassagemOrm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PassagemRepository extends JpaRepository<PassagemOrm, Integer> {
    List<PassagemOrm> findByIdPessoa(String idPessoa);

    List<PassagemOrm> findByIdPessoaAndDataBetween(String idPessoa, LocalDate dataInicio, LocalDate dataFim);
}