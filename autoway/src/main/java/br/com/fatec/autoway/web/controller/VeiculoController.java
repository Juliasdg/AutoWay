package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.VeiculoService;
import br.com.fatec.autoway.domain.model.Veiculo;
import br.com.fatec.autoway.infra.security.JwtUtil;
import br.com.fatec.autoway.web.dto.request.VeiculoRequest;
import br.com.fatec.autoway.web.dto.request.VeiculoAdminUpdateRequest;
import br.com.fatec.autoway.web.dto.response.VeiculoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApiResponse
@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService service;
    private final JwtUtil jwtUtil;

    public VeiculoController(VeiculoService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    // Criar veículo (cliente)
    @PostMapping
    public ResponseEntity<VeiculoResponse> criarVeiculo(
            @RequestBody VeiculoRequest body,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        Veiculo v = service.createVeiculo(idPessoa, body.placa().toUpperCase().trim());
        return ResponseEntity.status(201).body(toResponse(v));
    }

    // Listar veículos do cliente logado
    @GetMapping("/me")
    public ResponseEntity<List<VeiculoResponse>> listarMeusVeiculos(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        List<VeiculoResponse> veiculos = service.listByClient(idPessoa)
                .stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(veiculos);
    }

    // Listar todos os veículos (admin)
    @GetMapping
    public ResponseEntity<List<VeiculoResponse>> listarTodos() {
        List<VeiculoResponse> veiculos = service.listAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    // Ativar veículo com RFID (admin)
    @PutMapping("/{idVeiculo}/ativar")
    public ResponseEntity<VeiculoResponse> ativar(
            @PathVariable String idVeiculo,
            @RequestBody VeiculoAdminUpdateRequest body) {

        Veiculo v = service.assignRfid(idVeiculo, body.idRfid());
        return ResponseEntity.ok(toResponse(v));
    }

    // Inativar veículo (cliente ou admin)
    @PutMapping("/{idVeiculo}/inativar")
    public ResponseEntity<Void> inativar(
            @PathVariable String idVeiculo,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        boolean isAdmin = jwtUtil.getRolesFromJwtToken(token).contains("ADMIN");

        service.inactivate(idVeiculo, idPessoa, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // Reativar veículo (cliente ou admin)
    @PutMapping("/{idVeiculo}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable String idVeiculo,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        boolean isAdmin = jwtUtil.getRolesFromJwtToken(token).contains("ADMIN");

        service.reactivate(idVeiculo, idPessoa, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // Buscar veículo por ID (admin)
    @GetMapping("/{idVeiculo}")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable String idVeiculo) {
        Veiculo v = service.findById(idVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        return ResponseEntity.ok(toResponse(v));
    }

    @GetMapping("/search")
    public ResponseEntity<List<VeiculoResponse>> search(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String rfid
    ) {
        List<VeiculoResponse> result = service.search(placa, rfid).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ativos/count")
    public ResponseEntity<Map<String, Long>> countVeiculosAtivos() {
        long count = service.findAllAtivos().size();
        Map<String, Long> response = Map.of("quantidade", count);
        return ResponseEntity.ok(response);
    }



    // Conversor para Response DTO
    private VeiculoResponse toResponse(Veiculo v) {
        return new VeiculoResponse(
                v.getIdVeiculo(),
                v.getIdPessoa(),
                v.getPlaca(),
                v.getIdRfid(),
                v.isAtivo()
        );
    }
}
