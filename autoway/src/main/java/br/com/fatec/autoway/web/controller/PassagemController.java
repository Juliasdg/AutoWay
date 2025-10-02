package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.PassagemService;
import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.infra.security.JwtUtil;
import br.com.fatec.autoway.web.dto.request.PassagemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Tag(name = "Passagens", description = "Endpoints para gerenciamento de passagens de veículos")
@RestController
@RequestMapping("/api/passagens")
public class PassagemController {

    private final PassagemService service;
    private final JwtUtil jwtUtil;

    public PassagemController(PassagemService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Registrar passagem por RFID",
            description = "Registra a passagem de um veículo identificado pelo RFID, enviando notificações caso o veículo esteja desativado ou não registrado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Passagem registrada com sucesso",
                            content = @Content(schema = @Schema(implementation = Passagem.class))),
                    @ApiResponse(responseCode = "400", description = "Veículo desativado ou RFID inválido")
            }
    )
    @PostMapping
    public Passagem create(@RequestBody PassagemRequest request) {
        return service.createByRfid(request.rfid(), request.data(), request.hora());
    }

    @Operation(
            summary = "Listar todas as passagens",
            description = "Retorna todas as passagens registradas no sistema. Requer perfil ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de passagens retornada com sucesso",
                            content = @Content(schema = @Schema(implementation = Passagem.class)))
            }
    )
    @GetMapping("/all")
    public List<Passagem> listAll() {
        return service.listAll();
    }

    @Operation(
            summary = "Listar minhas passagens",
            description = "Retorna a lista de passagens do usuário autenticado no mês atual.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de passagens retornada com sucesso",
                            content = @Content(schema = @Schema(implementation = Passagem.class))),
                    @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou ausente")
            }
    )
    @GetMapping("/me")
    public List<Passagem> listMyPassagens(
            @Parameter(description = "JWT gerado no login", required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        return service.listByCurrentPessoa(idPessoa);
    }

    @Operation(
            summary = "Listar minhas passagens com filtro de período",
            description = "Retorna as passagens do usuário autenticado dentro do período especificado. Retorna também se o mês está fechado (início no dia 1 e fim no último dia do mês).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista filtrada retornada com sucesso",
                            content = @Content(schema = @Schema(example = "{ \"mesFechado\": false, \"passagens\": [...] }"))),
                    @ApiResponse(responseCode = "400", description = "Data início maior que data fim ou datas futuras"),
                    @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou ausente")
            }
    )
    @GetMapping("/me/filter")
    public Map<String, Object> listMyPassagensWithPeriod(
            @Parameter(description = "JWT gerado no login", required = true) @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "Data inicial do período (yyyy-MM-dd)", required = true) @RequestParam String dataInicio,
            @Parameter(description = "Data final do período (yyyy-MM-dd)", required = true) @RequestParam String dataFim) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inicio = LocalDate.parse(dataInicio, formatter);
        LocalDate fim = LocalDate.parse(dataFim, formatter);

        return service.listByCurrentPessoaWithPeriod(idPessoa, inicio, fim);
    }

    @Operation(
            summary = "Contar todas as passagens",
            description = "Retorna a quantidade total de passagens registradas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso",
                            content = @Content(schema = @Schema(example = "{ \"quantidade\": 123 }")))
            }
    )
    @GetMapping("/count")
    public Map<String, Long> countAllPassagens() {
        long count = service.listAll().size();
        return Map.of("quantidade", count);
    }

}
