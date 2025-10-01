package br.com.fatec.autoway.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Boleto(
        String idBoleto,
        String idPessoa,
        BigDecimal valorTotal,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<Passagem> passagens,
        StatusPagamento statusPagamento,
        LocalDate dataEmissao,
        LocalDate dataVencimento
) {}
