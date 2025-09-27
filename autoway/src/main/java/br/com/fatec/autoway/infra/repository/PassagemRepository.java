package br.com.fatec.autoway.infra.repository;

import br.com.fatec.autoway.infra.orm.PassagemOrm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassagemRepository extends JpaRepository<PassagemOrm, Integer> {
}
