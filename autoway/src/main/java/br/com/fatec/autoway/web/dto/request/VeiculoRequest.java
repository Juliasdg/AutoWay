package br.com.fatec.autoway.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VeiculoRequest(

        @NotBlank(message = "Placa vazia ou inválida. Por favor, verifique o valor inserido!")
        String placa
) {}
