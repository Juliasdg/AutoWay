package br.com.fatec.autoway.domain.port.persistence;

import br.com.fatec.autoway.domain.model.Passagem;
import java.util.List;

public interface PassagemRepositoryPort {

    Passagem save(Passagem passagem);

    List<Passagem> findAll();
}
