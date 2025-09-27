package br.com.fatec.autoway.domain.model;

import java.time.LocalDate;

public record
Pessoa(
        String id,
        String nome,
        String email,
        String senhaHash,
        TipoUsuario tipoUsuario,
        Boolean status,
        String telefone,
        String cpf,
        String cep,
        String endereco,
        String complemento,
        LocalDate dataNascimento,
        Integer vencimento
) {}
