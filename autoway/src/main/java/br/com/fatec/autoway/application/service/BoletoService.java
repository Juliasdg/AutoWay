package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.domain.model.StatusPagamento;
import br.com.fatec.autoway.domain.port.BoletoRepositoryPort;
import br.com.fatec.autoway.infra.messaging.BoletoProducer;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BoletoService {

    private static final Logger log = LoggerFactory.getLogger(BoletoService.class);

    private final BoletoRepositoryPort repository;
    private final PassagemService passagemService;
    private final BoletoProducer producer;
    private final EmailService emailService;
    private final PessoaService pessoaService;

    public BoletoService(BoletoRepositoryPort repository,
                         PassagemService passagemService,
                         BoletoProducer producer,
                         EmailService emailService,
                         PessoaService pessoaService) {
        this.repository = repository;
        this.passagemService = passagemService;
        this.producer = producer;
        this.emailService = emailService;
        this.pessoaService = pessoaService;
    }

    public BigDecimal calcularDebitoAtual(String idPessoa) {
        LocalDate hoje = LocalDate.now();
        List<Passagem> passagensPendentes = passagemService
                .listByCurrentPessoaWithPeriod(idPessoa,
                        LocalDate.of(hoje.getYear(), hoje.getMonth(), 1), hoje)
                .get("passagens") instanceof List<?> lista ? (List<Passagem>) lista : List.of();

        return passagensPendentes.stream()
                .map(p -> BigDecimal.valueOf(p.valor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Boleto gerarBoleto(String idPessoa, String emailUsuario, LocalDate inicio, LocalDate fim) {
        LocalDate hoje = LocalDate.now();

        if (inicio.isAfter(fim)) throw new IllegalArgumentException("Data início não pode ser maior que data fim");
        if (fim.isAfter(hoje)) throw new IllegalArgumentException("Não é permitido gerar boleto para datas futuras");

        List<Passagem> passagens = passagemService
                .listByCurrentPessoaWithPeriod(idPessoa, inicio, fim)
                .get("passagens") instanceof List<?> lista ? (List<Passagem>) lista : List.of();

        if (passagens.isEmpty()) throw new IllegalStateException("Não é possível gerar boleto sem passagens no período.");

        boolean existe = repository.findByPessoa(idPessoa).stream()
                .anyMatch(b ->
                        !b.statusPagamento().equals(StatusPagamento.pago) &&
                                !(b.dataInicio().isAfter(fim) || b.dataFim().isBefore(inicio))
                );

        if (existe) throw new IllegalStateException("Já existe um boleto gerado para este período.");

        BigDecimal valorTotal = passagens.stream()
                .map(p -> BigDecimal.valueOf(p.valor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        int diaVencimento = Math.min(
                pessoaService.findById(idPessoa).vencimento(),
                LocalDate.now().lengthOfMonth()
        );
        LocalDate dataVencimento = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), diaVencimento);

        Boleto boleto = new Boleto(
                UUID.randomUUID().toString(),
                idPessoa,
                valorTotal,
                inicio,
                fim,
                passagens,
                StatusPagamento.pendente,
                LocalDate.now(),
                dataVencimento
        );

        Boleto saved = repository.save(boleto);

        producer.enviarBoletoGerado(saved);

        if (emailUsuario != null && !emailUsuario.isBlank()) {
            try {
                emailService.enviarBoletoPorEmail(saved, emailUsuario);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        return saved;
    }

    public List<Boleto> listarPorPessoa(String idPessoa) {
        return repository.findByPessoa(idPessoa);
    }

    public Boleto buscarPorId(String idBoleto) {
        return repository.findById(idBoleto)
                .orElseThrow(() -> new IllegalArgumentException("Boleto não encontrado"));
    }

    public List<Boleto> listarTodos() {
        return repository.listarTodos();
    }

    public void ensureBoletoForPeriodIfPassagensExist(String idPessoa, String emailUsuario, LocalDate inicio, LocalDate fim) {
        try {
            List<Passagem> passagens = passagemService
                    .listByCurrentPessoaWithPeriod(idPessoa, inicio, fim)
                    .get("passagens") instanceof List<?> lista ? (List<Passagem>) lista : List.of();

            if (passagens.isEmpty()) {
                log.debug("Nenhuma passagem para pessoa {} no período {} - {}. Não gera boleto.", idPessoa, inicio, fim);
                return;
            }

            boolean existe = repository.findByPessoa(idPessoa).stream()
                    .anyMatch(b ->
                            !b.statusPagamento().equals(StatusPagamento.pago) &&
                                    !(b.dataInicio().isAfter(fim) || b.dataFim().isBefore(inicio))
                    );

            if (existe) {
                log.debug("Já existe boleto (não pago) para pessoa {} no período {} - {}. Skip.", idPessoa, inicio, fim);
                return;
            }

            gerarBoleto(idPessoa, emailUsuario, inicio, fim);
            log.info("Boleto gerado para pessoa {} no período {} - {}", idPessoa, inicio, fim);

        } catch (Exception e) {
            log.warn("Falha ao garantir boleto para pessoa {} no período {} - {}: {}", idPessoa, inicio, fim, e.getMessage(), e);
        }
    }
}
