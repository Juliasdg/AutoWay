package br.com.fatec.autoway.infra.repository.adapter;

import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.domain.port.persistence.BoletoRepositoryPort;
import br.com.fatec.autoway.infra.orm.BoletoOrm;
import br.com.fatec.autoway.infra.repository.BoletoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BoletoRepositoryAdapter implements BoletoRepositoryPort {

    private final BoletoRepository repository;
    private final ObjectMapper objectMapper;

    public BoletoRepositoryAdapter(BoletoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Boleto save(Boleto boleto) {
        try {
            BoletoOrm entity = fromDomain(boleto);
            return repository.save(entity).toDomain(objectMapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter passagens para JSON", e);
        }
    }

    @Override
    public List<Boleto> findByPessoa(String idPessoa) {
        return repository.findByIdPessoa(idPessoa).stream()
                .map(b -> {
                    try {
                        return b.toDomain(objectMapper);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao ler passagens do banco", e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Boleto> findByPeriodo(LocalDate inicio, LocalDate fim) {
        return repository.findByDataEmissaoBetween(inicio, fim).stream()
                .map(b -> {
                    try {
                        return b.toDomain(objectMapper);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao ler passagens do banco", e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Boleto> findById(String idBoleto) {
        return repository.findById(idBoleto)
                .map(b -> {
                    try {
                        return b.toDomain(objectMapper);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao ler passagens do banco", e);
                    }
                });
    }


    @Override
    public List<Boleto> listarTodos() {
        return repository.findAll().stream()
                .map(b -> {
                    try {
                        return b.toDomain(objectMapper);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao ler passagens do banco", e);
                    }
                })
                .collect(Collectors.toList());
    }


    private BoletoOrm fromDomain(Boleto boleto) throws JsonProcessingException {
        BoletoOrm entity = new BoletoOrm();
        entity.setIdBoleto(boleto.idBoleto());
        entity.setIdPessoa(boleto.idPessoa());
        entity.setValorTotal(boleto.valorTotal());
        entity.setDataInicio(boleto.dataInicio());
        entity.setDataFim(boleto.dataFim());
        entity.setPassagens(objectMapper.writeValueAsString(boleto.passagens()));
        entity.setStatusPagamento(boleto.statusPagamento());
        entity.setDataEmissao(boleto.dataEmissao());
        entity.setDataVencimento(boleto.dataVencimento());
        return entity;
    }
}
