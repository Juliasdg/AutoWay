package br.com.fatec.autoway.infra.orm;

import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.domain.model.StatusPagamento;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "boleto")
@Getter
@Setter
public class BoletoOrm {

    @Id
    @Column(name = "id_boleto", length = 36)
    private String idBoleto;

    @Column(name = "id_pessoa", nullable = false)
    private String idPessoa;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "passagens", columnDefinition = "json")
    private String passagens; // armazenado como JSON no banco

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamento statusPagamento;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;


    public static BoletoOrm fromDomain(Boleto boleto, ObjectMapper objectMapper) throws JsonProcessingException {
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


    public Boleto toDomain(ObjectMapper objectMapper) throws JsonProcessingException {
        List<Passagem> listaPassagens = objectMapper.readValue(
                passagens,
                new TypeReference<List<Passagem>>() {}
        );

        return new Boleto(
                idBoleto,
                idPessoa,
                valorTotal,
                dataInicio,
                dataFim,
                listaPassagens,
                statusPagamento,
                dataEmissao,
                dataVencimento
        );
    }

}
