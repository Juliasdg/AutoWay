package br.com.fatec.autoway.domain.port.persistence;

import br.com.fatec.autoway.domain.model.Veiculo;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepositoryPort {

    // CRUD básico
    void save(Veiculo veiculo);
    Optional<Veiculo> findById(String idVeiculo);
    List<Veiculo> findAll();

    // Busca específica
    List<Veiculo> findByPessoa(String idPessoa);
    Optional<Veiculo> findByPlaca(String placa);
    Optional<Veiculo> findByRfid(String idRfid);

    // Emails dos admins (simulação)
    List<String> listAdminEmails();
}
