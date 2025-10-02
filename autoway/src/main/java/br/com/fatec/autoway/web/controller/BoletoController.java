package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.BoletoPdfService;
import br.com.fatec.autoway.application.service.BoletoService;
import br.com.fatec.autoway.application.service.PessoaService;
import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.infra.scheduler.BoletoScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Boleto", description = "Endpoints para gerenciamento de boletos de pessoas")
@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    private final BoletoService service;
    private final BoletoPdfService boletoPdfService;
    private final PessoaService pessoaService;
    private final BoletoScheduler  boletoScheduler;

    public BoletoController(BoletoService service, BoletoPdfService boletoPdfService, PessoaService pessoaService, BoletoScheduler boletoScheduler) {
        this.service = service;
        this.boletoPdfService = boletoPdfService;
        this.pessoaService = pessoaService;
        this.boletoScheduler = boletoScheduler;
    }

    @Operation(summary = "Gerar boleto para uma pessoa em determinado período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boleto gerado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boleto.class),
                            examples = @ExampleObject(value = "{ \"id\": \"123\", \"idPessoa\": \"456\", \"valor\": 150.0, \"vencimento\": \"2025-10-10\" }"))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou boleto já existente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"erro\": \"Boleto já existe para esse período\" }"))),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"erro\": \"Pessoa não encontrada\" }")))
    })
    @PostMapping("/gerar")
    public Boleto gerar(
            @Parameter(description = "ID da pessoa", required = true) @RequestParam String idPessoa,
            @Parameter(description = "Data de início do período", required = true, example = "2025-10-01") @RequestParam LocalDate inicio,
            @Parameter(description = "Data de fim do período", required = true, example = "2025-10-31") @RequestParam LocalDate fim
    ) {
        Pessoa pessoa = pessoaService.findById(idPessoa);
        String email = pessoa.email();
        return service.gerarBoleto(idPessoa, email, inicio, fim);
    }


    @Operation(summary = "Listar boletos de uma pessoa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boletos retornados com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{ \"id\": \"123\", \"valor\": 150.0, \"vencimento\": \"2025-10-10\" }]"))),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"erro\": \"Pessoa não encontrada\" }")))
    })
    @GetMapping("/{idPessoa}")
    public List<Boleto> listar(
            @Parameter(description = "ID da pessoa", required = true) @PathVariable String idPessoa) {
        return service.listarPorPessoa(idPessoa);
    }

    @Operation(summary = "Baixar boleto em PDF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF retornado com sucesso",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "404", description = "Boleto não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"erro\": \"Boleto não encontrado\" }")))
    })
    @GetMapping("/pdf/{idBoleto}")
    public ResponseEntity<byte[]> baixarBoleto(
            @Parameter(description = "ID do boleto", required = true) @PathVariable String idBoleto) {
        Boleto boleto = service.buscarPorId(idBoleto);
        byte[] pdf = boletoPdfService.gerarPdfBoleto(boleto);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=boleto.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Consultar débito atual de uma pessoa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Débito calculado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"debito\": 450.0 }"))),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"erro\": \"Pessoa não encontrada\" }")))
    })
    @GetMapping("/debito/{idPessoa}")
    public BigDecimal consultarDebito(
            @Parameter(description = "ID da pessoa", required = true) @PathVariable String idPessoa) {
        return service.calcularDebitoAtual(idPessoa);
    }

    @Operation(summary = "Listar todos os boletos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de boletos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{ \"id\": \"123\", \"valor\": 150.0, \"vencimento\": \"2025-10-10\" }]")))
    })
    @GetMapping
    public List<Boleto> listarTodos() {
        return service.listarTodos();
    }

    @Operation(summary = "Trigger diário manual para geração de boletos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trigger diário executado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"mensagem\": \"Daily trigger executed\" }")))
    })
    @PostMapping("/trigger/daily")
    public ResponseEntity<String> triggerDaily() {
        boletoScheduler.dailyCheckAtMidnight();
        return ResponseEntity.ok("Daily trigger executed");
    }

    @Operation(summary = "Trigger mensal manual para geração de boletos do mês anterior")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trigger mensal executado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"mensagem\": \"Monthly trigger executed\" }")))
    })
    @PostMapping("/trigger/monthly")
    public ResponseEntity<String> triggerMonthly() {
        boletoScheduler.monthlyGeneratePreviousMonth();
        return ResponseEntity.ok("Monthly trigger executed");
    }

    @Operation(summary = "Contar a quantidade total de boletos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade de boletos retornada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"quantidade\": 25 }")))
    })
    @GetMapping("/count")
    public Map<String, Long> countAllBoletos() {
        long count = service.listarTodos().size();
        return Map.of("quantidade", count);
    }


}
