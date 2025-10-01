package br.com.fatec.autoway.infra.repository.adapter;

import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.domain.port.PassagemRepositoryPort;
import br.com.fatec.autoway.infra.orm.PassagemOrm;
import br.com.fatec.autoway.infra.repository.jpa.PassagemRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PassagemRepositoryAdapter implements PassagemRepositoryPort {

    private final PassagemRepository jpa;

    public PassagemRepositoryAdapter(PassagemRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Passagem save(Passagem p) {
        PassagemOrm orm = new PassagemOrm(
                p.id(),
                p.idVeiculo(),
                p.idPessoa(),
                p.local(),
                p.data(),
                p.hora(),
                p.valor()
        );
        PassagemOrm saved = jpa.save(orm);
        return new Passagem(
                saved.getId(),
                saved.getIdVeiculo(),
                saved.getIdPessoa(),
                saved.getLocal(),
                saved.getData(),
                saved.getHora(),
                saved.getValor()
        );
    }

    @Override
    public List<Passagem> findAll() {
        return jpa.findAll().stream().map(orm -> new Passagem(
                orm.getId(),
                orm.getIdVeiculo(),
                orm.getIdPessoa(),
                orm.getLocal(),
                orm.getData(),
                orm.getHora(),
                orm.getValor()
        )).collect(Collectors.toList());
    }

    @Override
    public List<Passagem> findByPessoa(String pessoaId) {
        return jpa.findByIdPessoa(pessoaId)
                .stream()
                .map(orm -> new Passagem(
                        orm.getId(),
                        orm.getIdVeiculo(),
                        orm.getIdPessoa(),
                        orm.getLocal(),
                        orm.getData(),
                        orm.getHora(),
                        orm.getValor()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Passagem> findByPessoaAndPeriodo(String pessoaId, LocalDate dataInicio, LocalDate dataFim) {
        return jpa.findByIdPessoaAndDataBetween(pessoaId, dataInicio, dataFim)
                .stream()
                .map(orm -> new Passagem(
                        orm.getId(),
                        orm.getIdVeiculo(),
                        orm.getIdPessoa(),
                        orm.getLocal(),
                        orm.getData(),
                        orm.getHora(),
                        orm.getValor()
                ))
                .collect(Collectors.toList());
    }


}
