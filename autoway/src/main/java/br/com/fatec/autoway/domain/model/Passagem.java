package br.com.fatec.autoway.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Passagem(
        Integer id,
        String idVeiculo,
        String idPessoa,
        String local,
        LocalDate data,
        LocalTime hora,
        Double valor
) {}
