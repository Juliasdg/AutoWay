package br.com.fatec.autoway.infra.scheduler;

import br.com.fatec.autoway.application.service.BoletoService;
import br.com.fatec.autoway.application.service.PessoaService;
import br.com.fatec.autoway.domain.model.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BoletoScheduler {

    private static final Logger log = LoggerFactory.getLogger(BoletoScheduler.class);

    private static final Set<Integer> ALLOWED_VENCIMENTO_DAYS = Set.of(5, 20);

    private final BoletoService boletoService;
    private final PessoaService pessoaService;
    private final int retroactiveDays;

    public BoletoScheduler(
            BoletoService boletoService,
            PessoaService pessoaService,
            @Value("${boleto.scheduler.retroactive-days:7}") int retroactiveDays
    ) {
        this.boletoService = boletoService;
        this.pessoaService = pessoaService;
        this.retroactiveDays = retroactiveDays;
    }

    /**
     * Executa ao iniciar a aplicação → processa retroativamente os últimos N dias
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        log.info("Scheduler startup check iniciando em {} (retroagindo {} dias)",
                LocalDateTime.now(), retroactiveDays);

        LocalDate hoje = LocalDate.now();

        for (int i = retroactiveDays; i >= 0; i--) {
            LocalDate dia = hoje.minusDays(i);
            log.info("[Retroativo] Iniciando processamento do dia {}", dia);
            try {
                processDay(dia, true);
            } catch (Exception e) {
                log.warn("[Retroativo] Erro ao processar dia {}: {}", dia, e.getMessage(), e);
            }
        }
    }

    /**
     * Trigger diário às 00:00 → processa boletos de acordo com o dia de vencimento configurado
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyCheckAtMidnight() {
        LocalDate hoje = LocalDate.now();
        log.info("[Diário] Scheduler rodando em {}", LocalDateTime.now());
        processDay(hoje, false);
    }

    private void processDay(LocalDate date, boolean forced) {
        List<Pessoa> pessoas = pessoaService.findAllAtivos();

        // Filtra apenas quem está com vencimento permitido ou se for forçado
        List<Pessoa> pessoasFiltradas = pessoas.stream()
                .filter(p -> forced || (p.vencimento() != null && ALLOWED_VENCIMENTO_DAYS.contains(p.vencimento())))
                .collect(Collectors.toList());

        pessoasFiltradas.parallelStream().forEach(pessoa -> {
            try {
                int effectiveVencimento = Math.min(pessoa.vencimento() == null ? 1 : pessoa.vencimento(),
                        YearMonth.from(date).lengthOfMonth());

                if (!forced && date.getDayOfMonth() < effectiveVencimento) {
                    return; // ainda não é dia de vencimento
                }

                YearMonth targetMonth = YearMonth.from(date).minusMonths(1);
                LocalDate inicio = targetMonth.atDay(1);
                LocalDate fim = targetMonth.atEndOfMonth();

                log.info("Processando pessoa {} (vencimento {}) para período {} - {}",
                        pessoa.id(), pessoa.vencimento(), inicio, fim);

                boletoService.ensureBoletoForPeriodIfPassagensExist(
                        pessoa.id(), pessoa.email(), inicio, fim
                );
            } catch (Exception e) {
                log.warn("Erro ao processar pessoa {} no scheduler: {}", pessoa.id(), e.getMessage(), e);
            }
        });
    }

    /**
     * Scheduler de virada do mês → garante geração de boletos do mês anterior para todos
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlyGeneratePreviousMonth() {
        LocalDate hoje = LocalDate.now();
        YearMonth mesAnterior = YearMonth.from(hoje.minusMonths(1));
        LocalDate inicio = mesAnterior.atDay(1);
        LocalDate fim = mesAnterior.atEndOfMonth();

        log.info("[Mensal] Scheduler virada do mês rodando em {} → período {} - {}",
                LocalDateTime.now(), inicio, fim);

        List<Pessoa> pessoas = pessoaService.findAllAtivos();
        pessoas.parallelStream().forEach(pessoa -> {
            try {
                log.info("[Mensal] Processando pessoa {} (vencimento {})", pessoa.id(), pessoa.vencimento());
                boletoService.ensureBoletoForPeriodIfPassagensExist(
                        pessoa.id(), pessoa.email(), inicio, fim
                );
            } catch (Exception e) {
                log.warn("[Mensal] Erro ao gerar boleto para pessoa {}: {}", pessoa.id(), e.getMessage(), e);
            }
        });
    }
}
