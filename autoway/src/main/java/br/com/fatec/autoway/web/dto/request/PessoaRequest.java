package br.com.fatec.autoway.web.dto.request;

import jakarta.validation.constraints.*;

public record PessoaRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotBlank(message = "CEP é obrigatório")
        String cep,

        @NotBlank(message = "Endereço é obrigatório")
        String endereco,

        @NotBlank(message = "Complemento é obrigatório")
        String complemento,

        @NotBlank(message = "Data de nascimento é obrigatória")
        String dataNascimento,

        @NotNull(message = "Dia de vencimento é obrigatório")
        Integer vencimento
) {}
