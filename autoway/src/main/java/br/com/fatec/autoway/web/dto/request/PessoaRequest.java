package br.com.fatec.autoway.web.dto.request;

public record PessoaRequest(
        String nome,
        String email,
        String senha,
        String telefone,
        String cpf,
        String cep,
        String endereco,
        String complemento,
        String dataNascimento,
        Integer vencimento
) {}
