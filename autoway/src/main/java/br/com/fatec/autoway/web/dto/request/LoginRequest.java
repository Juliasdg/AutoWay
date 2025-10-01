package br.com.fatec.autoway.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email e Senha são obrigatórios")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Email e Senha são obrigatória")
        String senha
) {}
