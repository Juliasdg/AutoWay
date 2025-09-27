package br.com.fatec.autoway.web.dto.response;

public record PessoaResponse(
        String id,
        String nome,
        String email,
        String tipoUsuario,
        Boolean status,
        String telefone,
        String cpf,
        String cep,
        String endereco,
        String complemento,
        String dataNascimento,
        Integer vencimento
) {}
