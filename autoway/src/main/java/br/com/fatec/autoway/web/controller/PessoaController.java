package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.PessoaService;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.infra.security.JwtUtil;
import br.com.fatec.autoway.web.dto.request.ChangePasswordRequest;
import br.com.fatec.autoway.web.dto.request.PessoaRequest;
import br.com.fatec.autoway.web.dto.request.PessoaUpdateRequest;
import br.com.fatec.autoway.web.dto.response.ErrorResponse;
import br.com.fatec.autoway.web.dto.response.PessoaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Tag(name = "Pessoas", description = "Gerenciamento de usuários do sistema")
@ApiResponse
@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {

    private final PessoaService service;
    private final JwtUtil jwtUtil;


    public PessoaController(PessoaService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Confirma usuário pelo token de ativação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário confirmado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmUser(@RequestParam String token) {
        try {
            String message = service.confirmUser(token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Cria um novo usuário (cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = PessoaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PessoaResponse> create(@RequestBody PessoaRequest req) {
        Pessoa saved = service.create(req, "cliente");
        return ResponseEntity.status(201).body(toResponse(saved));
    }

    @Operation(summary = "Lista todos os usuários (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    public ResponseEntity<List<PessoaResponse>> listAll(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.getRoleFromJwtToken(token);

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        var list = service.listAll().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Atualiza dados do próprio usuário")
    @PutMapping("/me")
    public ResponseEntity<PessoaResponse> updateMe(
            @RequestBody PessoaUpdateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);

        Pessoa updated = service.updateOwn(email, request);
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Altera senha do próprio usuário")
    @PutMapping("/me/password")
    public ResponseEntity<?> changeMyPassword(
            @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String timestamp = java.time.LocalDateTime.now().toString();
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromJwtToken(token);

            if (!request.newPassword().equals(request.confirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Nova senha e confirmação não coincidem", timestamp));
            }

            service.updatePassword(email, request.currentPassword(), request.newPassword());

            return ResponseEntity.ok("Senha alterada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), timestamp));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, "Erro interno do servidor", timestamp));
        }
    }

    @Operation(summary = "Atualiza dados de um usuário (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponse> updatePessoa(
            @PathVariable String id,
            @RequestBody PessoaUpdateRequest request
    ) {
        Pessoa updated = service.updateAsAdmin(id, request);
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Inativa um usuário")
    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<Void> inactivate(@PathVariable String id) {
        service.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Inativa um usuário")
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable String id) {
        service.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtém dados do próprio usuário autenticado")
    @GetMapping("/me")
    public ResponseEntity<PessoaResponse> getMyData(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        Pessoa p = service.findByEmail(email);
        return ResponseEntity.ok(toResponse(p));
    }

    @Operation(summary = "Busca usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponse> getById(@PathVariable String id,
                                                  @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.getRoleFromJwtToken(token);

        if ("CLIENTE".equalsIgnoreCase(role)) {
            Pessoa p = service.findById(id);
            String email = jwtUtil.getEmailFromJwtToken(token);
            if (!p.email().equals(email)) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(toResponse(p));
        }

        Pessoa p = service.findById(id);
        return ResponseEntity.ok(toResponse(p));
    }

    @Operation(summary = "Pesquisa usuários por nome")
    @GetMapping("/search")
    public ResponseEntity<List<PessoaResponse>> search(
            @Parameter(description = "Nome (ou parte) para filtrar")
            @RequestParam(required = false) String nome
    ) {
        List<PessoaResponse> result = service.search(nome).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Conta quantidade de usuários ativos")
    @GetMapping("/ativos/count")
    public ResponseEntity<Map<String, Long>> countAtivos() {
        long count = service.findAllAtivos().size();
        Map<String, Long> response = Map.of("quantidade", count);
        return ResponseEntity.ok(response);
    }

    private PessoaResponse toResponse(Pessoa p) {
        return new PessoaResponse(p.id(), p.nome(), p.email(), p.tipoUsuario().name(),
                p.status(), p.telefone(), p.cpf(), p.cep(), p.endereco(), p.complemento(), p.dataNascimento() != null ? p.dataNascimento().toString() : null, p.vencimento()
        );
    }
}
