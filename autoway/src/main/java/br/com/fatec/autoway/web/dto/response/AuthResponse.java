package br.com.fatec.autoway.web.dto.response;

import br.com.fatec.autoway.domain.model.TipoUsuario;

public record AuthResponse(String token, String userId, TipoUsuario tipoUsuario) {}
