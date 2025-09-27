package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.PassagemService;
import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.infra.security.JwtUtil;
import br.com.fatec.autoway.web.dto.request.PassagemRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@ApiResponse
@RestController
@RequestMapping("/api/passagens")
public class PassagemController {

    private final PassagemService service;
    private final JwtUtil jwtUtil;

    public PassagemController(PassagemService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Passagem create(@RequestBody PassagemRequest request) {
        return service.createByRfid(request.rfid(), request.data(), request.hora());
    }

    @GetMapping("/all")
    public List<Passagem> listAll() {
        return service.listAll();
    }

    @GetMapping("/me")
    public List<Passagem> listMyPassagens(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        return service.listByCurrentPessoa(idPessoa);
    }

    @GetMapping("/me/filter")
    public Map<String, Object> listMyPassagensWithPeriod(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromJwtToken(token);
        String idPessoa = service.findPessoaIdByEmail(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inicio = LocalDate.parse(dataInicio, formatter);
        LocalDate fim = LocalDate.parse(dataFim, formatter);

        return service.listByCurrentPessoaWithPeriod(idPessoa, inicio, fim);
    }
}
