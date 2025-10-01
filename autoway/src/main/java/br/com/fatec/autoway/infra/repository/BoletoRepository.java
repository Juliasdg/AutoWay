package br.com.fatec.autoway.infra.repository;

import br.com.fatec.autoway.infra.orm.BoletoOrm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BoletoRepository extends JpaRepository<BoletoOrm, String> {
    List<BoletoOrm> findByIdPessoa(String idPessoa);
    List<BoletoOrm> findByDataEmissaoBetween(LocalDate inicio, LocalDate fim);
    List<BoletoOrm> findAll();
}
