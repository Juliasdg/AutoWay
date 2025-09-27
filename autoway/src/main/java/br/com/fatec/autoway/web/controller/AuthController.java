package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.PessoaService;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.infra.security.JwtUtil;
import br.com.fatec.autoway.web.dto.request.*;
import br.com.fatec.autoway.web.dto.response.AuthResponse;
import br.com.fatec.autoway.web.dto.response.ErrorResponse;
import br.com.fatec.autoway.web.dto.response.PessoaResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@ApiResponse
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PessoaService pessoaService;
    private final JwtUtil jwtUtil;

    @Value("${app.admin.secret}")
    private String adminSecret;

    public AuthController(PessoaService pessoaService, JwtUtil jwtUtil) {
        this.pessoaService = pessoaService;
        this.jwtUtil = jwtUtil;
    }

    // DTOs internos para requests JSON

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            pessoaService.initiatePasswordReset(request.email());
            return ResponseEntity.ok("Código de redefinição enviado para seu e-mail.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, "Erro interno do servidor", LocalDateTime.now().toString()));
        }
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody VerifyResetRequest request) {
        try {
            boolean valid = pessoaService.verifyResetCode(request.email(), request.code());
            return ResponseEntity.ok(valid);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, "Erro interno do servidor", LocalDateTime.now().toString()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            pessoaService.resetPassword(request.email(), request.code(), request.newPassword());
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, "Erro interno do servidor", LocalDateTime.now().toString()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String email,
            @RequestBody ChangePasswordRequest request) {
        try {
            // valida se nova senha e confirmação são iguais
            if (!request.newPassword().equals(request.confirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Nova senha e confirmação não coincidem", LocalDateTime.now().toString()));
            }

            pessoaService.updatePassword(email, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok("Senha alterada com sucesso!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, "Erro interno do servidor", LocalDateTime.now().toString()));
        }
    }


    // Mantém os endpoints de registro e login como estavam
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody PessoaRequest request,
            @RequestParam(defaultValue = "cliente") String role,
            @RequestParam(required = false) String adminKey) {

        if ("admin".equalsIgnoreCase(role)) {
            if (adminKey == null || !adminKey.equals(adminSecret)) {
                ErrorResponse err = new ErrorResponse(
                        400,
                        "Chave de admin inválida.",
                        LocalDateTime.now().toString()
                );
                return ResponseEntity.badRequest().body(err);
            }
        }

        try {
            pessoaService.validatePessoaRequest(request);
            Pessoa saved = pessoaService.create(request, role);

            PessoaResponse response = new PessoaResponse(
                    saved.id(),
                    saved.nome(),
                    saved.email(),
                    saved.tipoUsuario().name(),
                    saved.status(),
                    saved.telefone(),
                    saved.cpf(),
                    saved.cep(),
                    saved.endereco(),
                    saved.complemento(),
                    saved.dataNascimento() != null ? saved.dataNascimento().toString() : null,
                    saved.vencimento()
            );

            return ResponseEntity.status(201).body(response);

        } catch (IllegalArgumentException e) {
            ErrorResponse err = new ErrorResponse(
                    400,
                    e.getMessage(),
                    LocalDateTime.now().toString()
            );
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            ErrorResponse err = new ErrorResponse(
                    500,
                    "Erro interno do servidor.",
                    LocalDateTime.now().toString()
            );
            return ResponseEntity.status(500).body(err);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            Pessoa user = pessoaService.findByEmail(request.email());
            if (user == null || !user.status()) {
                return ResponseEntity.status(401).build();
            }

            boolean ok = pessoaService.checkPassword(request.senha(), user.senhaHash());
            if (!ok) {
                return ResponseEntity.status(401).build();
            }

            String token = jwtUtil.generateToken(user.email(), user.tipoUsuario().name());

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);

            return ResponseEntity.ok(
                    new AuthResponse(token, user.id().toString(), user.tipoUsuario())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
