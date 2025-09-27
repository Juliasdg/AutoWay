package br.com.fatec.autoway.web.dto.request;

public record ResetPasswordRequest(String email, String code, String newPassword) {}
