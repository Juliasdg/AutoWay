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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmUser(@RequestParam String token) {
        try {
            String message = service.confirmUser(token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<PessoaResponse> create(@RequestBody PessoaRequest req) {
        Pessoa saved = service.create(req, "cliente");
        return ResponseEntity.status(201).body(toResponse(saved));
    }


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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponse> updatePessoa(
            @PathVariable String id,
            @RequestBody PessoaUpdateRequest request
    ) {
        Pessoa updated = service.updateAsAdmin(id, request);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<Void> inactivate(@PathVariable String id) {
        service.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable String id) {
        service.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<PessoaResponse> getMyData(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        Pessoa p = service.findByEmail(email);
        return ResponseEntity.ok(toResponse(p));
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<PessoaResponse>> search(
            @RequestParam(required = false) String nome
    ) {
        List<PessoaResponse> result = service.search(nome).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

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
