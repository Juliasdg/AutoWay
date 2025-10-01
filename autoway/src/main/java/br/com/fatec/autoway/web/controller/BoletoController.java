package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.BoletoPdfService;
import br.com.fatec.autoway.application.service.BoletoService;
import br.com.fatec.autoway.application.service.PessoaService;
import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.infra.scheduler.BoletoScheduler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/gerar")
    public Boleto gerar(
            @RequestParam String idPessoa,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        Pessoa pessoa = pessoaService.findById(idPessoa);
        String email = pessoa.email();
        return service.gerarBoleto(idPessoa, email, inicio, fim);
    }


    @GetMapping("/{idPessoa}")
    public List<Boleto> listar(@PathVariable String idPessoa) {
        return service.listarPorPessoa(idPessoa);
    }

    @GetMapping("/pdf/{idBoleto}")
    public ResponseEntity<byte[]> baixarBoleto(@PathVariable String idBoleto) {
        Boleto boleto = service.buscarPorId(idBoleto);
        byte[] pdf = boletoPdfService.gerarPdfBoleto(boleto);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=boleto.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/debito/{idPessoa}")
    public BigDecimal consultarDebito(@PathVariable String idPessoa) {
        return service.calcularDebitoAtual(idPessoa);
    }

    @GetMapping
    public List<Boleto> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping("/trigger/daily")
    public ResponseEntity<String> triggerDaily() {
        boletoScheduler.dailyCheckAtMidnight();
        return ResponseEntity.ok("Daily trigger executed");
    }

    @PostMapping("/trigger/monthly")
    public ResponseEntity<String> triggerMonthly() {
        boletoScheduler.monthlyGeneratePreviousMonth();
        return ResponseEntity.ok("Monthly trigger executed");
    }

    @GetMapping("/count")
    public Map<String, Long> countAllBoletos() {
        long count = service.listarTodos().size();
        return Map.of("quantidade", count);
    }


}
