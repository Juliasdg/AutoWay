package br.com.fatec.autoway.domain.port;

import br.com.fatec.autoway.domain.model.Veiculo;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepositoryPort {
    void save(Veiculo veiculo);
    Optional<Veiculo> findById(String idVeiculo);
    List<Veiculo> findAll();
    List<Veiculo> findByPessoa(String idPessoa);
    Optional<Veiculo> findByPlaca(String placa);
    Optional<Veiculo> findByRfid(String idRfid);
    List<String> listAdminEmails();
}
