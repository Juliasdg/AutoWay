package br.com.fatec.autoway.web.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record PassagemRequest(
        String rfid,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora
) {}
