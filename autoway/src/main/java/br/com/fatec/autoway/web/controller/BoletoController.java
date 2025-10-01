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

    // Gera boleto
    @PostMapping("/gerar")
    public Boleto gerar(
            @RequestParam String idPessoa,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        Pessoa pessoa = pessoaService.findById(idPessoa);
        String email = pessoa.email(); // pega do cadastro
        return service.gerarBoleto(idPessoa, email, inicio, fim);
    }


    // Lista boletos de uma pessoa
    @GetMapping("/{idPessoa}")
    public List<Boleto> listar(@PathVariable String idPessoa) {
        return service.listarPorPessoa(idPessoa);
    }

    // Baixa ou visualiza PDF do boleto
    @GetMapping("/pdf/{idBoleto}")
    public ResponseEntity<byte[]> baixarBoleto(@PathVariable String idBoleto) {
        Boleto boleto = service.buscarPorId(idBoleto);
        byte[] pdf = boletoPdfService.gerarPdfBoleto(boleto);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=boleto.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // BoletoController.java
    @GetMapping("/debito/{idPessoa}")
    public BigDecimal consultarDebito(@PathVariable String idPessoa) {
        return service.calcularDebitoAtual(idPessoa);
    }

    // Lista todos os boletos (admin)
    @GetMapping
    public List<Boleto> listarTodos() {
        return service.listarTodos();
    }

    // Trigger manual (admin/dev) que executa a lógica do scheduler diário IMEDIATAMENTE
    @PostMapping("/trigger/daily")
    public ResponseEntity<String> triggerDaily() {
        // injetar BoletoScheduler (ou expor um método em service)
        boletoScheduler.dailyCheckAtMidnight(); // ou chamar método público que você exponha
        return ResponseEntity.ok("Daily trigger executed");
    }

    // Trigger manual para geração mensal (virada)
    @PostMapping("/trigger/monthly")
    public ResponseEntity<String> triggerMonthly() {
        boletoScheduler.monthlyGeneratePreviousMonth();
        return ResponseEntity.ok("Monthly trigger executed");
    }


}
