package br.com.fatec.autoway.web.dto.request;

public record PessoaUpdateRequest(
        String nome,
        String telefone,
        String cep,
        String endereco,
        String complemento,
        String dataNascimento,
        Integer vencimento
) {}
