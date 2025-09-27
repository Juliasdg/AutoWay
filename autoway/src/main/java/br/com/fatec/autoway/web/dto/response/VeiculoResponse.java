package br.com.fatec.autoway.web.dto.response;

public record VeiculoResponse(
        String idVeiculo,
        String idPessoa,
        String placa,
        String idRfid,
        boolean ativo
) {}
