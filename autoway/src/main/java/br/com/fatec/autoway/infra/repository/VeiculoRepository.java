package br.com.fatec.autoway.infra.repository;

import br.com.fatec.autoway.infra.orm.VeiculoOrm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;


public interface VeiculoRepository extends JpaRepository<VeiculoOrm, String> {
    Optional<VeiculoOrm> findByPlaca(String placa);
    Optional<VeiculoOrm> findByRfid(String rfid);
    List<VeiculoOrm> findByIdPessoa(String idPessoa);
}
