package br.com.fatec.autoway.domain.port.persistence;

import br.com.fatec.autoway.domain.model.Passagem;

import java.time.LocalDate;
import java.util.List;

public interface PassagemRepositoryPort {

    Passagem save(Passagem passagem);

    List<Passagem> findAll();

    List<Passagem> findByPessoaAndPeriodo(String pessoaId, LocalDate inicio, LocalDate fim);

    List<Passagem> findByPessoa(String pessoaId);


}
