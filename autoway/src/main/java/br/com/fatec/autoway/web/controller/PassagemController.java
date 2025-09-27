package br.com.fatec.autoway.web.controller;

import br.com.fatec.autoway.application.service.PassagemService;
import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.web.dto.request.PassagemRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/passagens")
public class PassagemController {

    private final PassagemService service;

    public PassagemController(PassagemService service) {
        this.service = service;
    }

    @PostMapping
    public Passagem create(@RequestBody PassagemRequest request) {
        return service.createByRfid(request.rfid(), request.data(), request.hora());
    }

    @GetMapping
    public List<Passagem> listAll() {
        return service.listAll();
    }
}
