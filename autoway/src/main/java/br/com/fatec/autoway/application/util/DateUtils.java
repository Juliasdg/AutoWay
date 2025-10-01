package br.com.fatec.autoway.application.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    public static LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) return null;
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Formato de data inválido: " + input);
    }

    public static void validateAdult(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return;
        LocalDate today = LocalDate.now();
        if (dateOfBirth.plusYears(18).isAfter(today)) {
            throw new IllegalArgumentException("O usuário deve ser maior de 18 anos.");
        }
    }
}
