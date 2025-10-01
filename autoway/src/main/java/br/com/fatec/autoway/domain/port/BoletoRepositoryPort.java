package br.com.fatec.autoway.domain.port;

import br.com.fatec.autoway.domain.model.Boleto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BoletoRepositoryPort {
    Boleto save(Boleto boleto);
    List<Boleto> findByPessoa(String idPessoa);
    List<Boleto> findByPeriodo(LocalDate inicio, LocalDate fim);
    Optional<Boleto> findById(String idBoleto);

    List<Boleto> listarTodos();
}
