package br.com.fatec.autoway.web.dto.response;

public record ErrorResponse(
        int status,
        String message,
        String timestamp
) {}
