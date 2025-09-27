package br.com.fatec.autoway.web.dto.request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
