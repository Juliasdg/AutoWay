package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.VeiculoService;
import br.com.fatec.autoway.domain.model.Veiculo;
import br.com.fatec.autoway.infra.security.JwtUtil;
import br.com.fatec.autoway.web.dto.request.VeiculoRequest;
import br.com.fatec.autoway.web.dto.request.VeiculoAdminUpdateRequest;
import br.com.fatec.autoway.web.dto.response.VeiculoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Veículos", description = "Endpoints para gerenciamento de veículos")
@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService service;
    private final JwtUtil jwtUtil;

    public VeiculoController(VeiculoService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Cadastrar um novo veículo",
            description = "Permite que o cliente cadastre um veículo associado à sua conta. O veículo ficará aguardando validação do admin.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso",
                            content = @Content(schema = @Schema(implementation = VeiculoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Placa inválida ou já cadastrada"),
                    @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou ausente")
            }
    )
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

    @Operation(
            summary = "Listar meus veículos",
            description = "Retorna a lista de veículos cadastrados pelo cliente autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                            content = @Content(schema = @Schema(implementation = VeiculoResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou ausente")
            }
    )
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

    @Operation(
            summary = "Listar todos os veículos (Admin)",
            description = "Retorna todos os veículos cadastrados no sistema. Requer perfil ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de veículos retornada")
            }
    )
    @GetMapping
    public ResponseEntity<List<VeiculoResponse>> listarTodos() {
        List<VeiculoResponse> veiculos = service.listAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @Operation(
            summary = "Ativar veículo (associar RFID)",
            description = "Permite que o admin valide um veículo, associando um RFID e ativando-o.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo ativado com sucesso",
                            content = @Content(schema = @Schema(implementation = VeiculoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "RFID já cadastrado ou veículo inexistente")
            }
    )
    @PutMapping("/{idVeiculo}/ativar")
    public ResponseEntity<VeiculoResponse> ativar(
            @PathVariable String idVeiculo,
            @RequestBody VeiculoAdminUpdateRequest body) {

        Veiculo v = service.assignRfid(idVeiculo, body.idRfid());
        return ResponseEntity.ok(toResponse(v));
    }

    @Operation(
            summary = "Inativar veículo",
            description = "Permite que o cliente (ou admin) inative um veículo. O veículo não poderá mais ser usado até ser reativado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Veículo inativado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Ação não permitida ou veículo inexistente")
            }
    )
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

    @Operation(
            summary = "Reativar veículo",
            description = "Permite que o cliente (ou admin) reative um veículo anteriormente inativado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Veículo reativado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Ação não permitida ou veículo inexistente")
            }
    )
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

    @Operation(
            summary = "Buscar veículo por ID",
            description = "Retorna as informações de um veículo específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo encontrado",
                            content = @Content(schema = @Schema(implementation = VeiculoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
            }
    )
    @GetMapping("/{idVeiculo}")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable String idVeiculo) {
        Veiculo v = service.findById(idVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        return ResponseEntity.ok(toResponse(v));
    }

    @Operation(
            summary = "Buscar veículos por filtros",
            description = "Permite buscar veículos pelo número da placa ou pelo RFID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
            }
    )
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

    @Operation(
            summary = "Contar veículos ativos",
            description = "Retorna a quantidade de veículos ativos no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso",
                            content = @Content(schema = @Schema(example = "{ \"quantidade\": 42 }")))
            }
    )
    @GetMapping("/ativos/count")
    public ResponseEntity<Map<String, Long>> countVeiculosAtivos() {
        long count = service.findAllAtivos().size();
        Map<String, Long> response = Map.of("quantidade", count);
        return ResponseEntity.ok(response);
    }

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
