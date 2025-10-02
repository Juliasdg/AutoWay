package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.domain.model.TipoUsuario;
import br.com.fatec.autoway.domain.model.Veiculo;
import br.com.fatec.autoway.domain.port.PassagemRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PassagemService {

    private final PassagemRepositoryPort repository;
    private final EmailService emailService;
    private final VeiculoService veiculoService;
    private final PessoaService pessoaService;

    public PassagemService(PassagemRepositoryPort repository,
                           EmailService emailService,
                           VeiculoService veiculoService,
                           PessoaService pessoaService) {
        this.repository = repository;
        this.emailService = emailService;
        this.veiculoService = veiculoService;
        this.pessoaService = pessoaService;
    }

    public Passagem createByRfid(String rfid, LocalDate data, LocalTime hora) {
        Veiculo veiculo;
        try {
            veiculo = veiculoService.findByRfid(rfid);
        } catch (RuntimeException ex) {

            List<String> adminEmails = pessoaService.findAllAtivos().stream()
                    .filter(p -> p.tipoUsuario() == TipoUsuario.admin)
                    .map(Pessoa::email)
                    .toList();


            emailService.sendGenericEmailToAdmins(
                    adminEmails,
                    "Falha ao registrar passagem",
                    "Tentativa de passagem com RFID não registrado: " + rfid
            );
            throw new IllegalArgumentException("Veículo não registrado ou RFID inválido");
        }

        Pessoa pessoa = pessoaService.findById(veiculo.getIdPessoa());

        if (!veiculo.isAtivo()) {
            List<String> adminEmails = pessoaService.findAllAtivos().stream()
                    .filter(p -> p.tipoUsuario() == TipoUsuario.admin)
                    .map(Pessoa::email)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = data.format(formatter);

            emailService.sendGenericEmailToAdmins(
                    adminEmails,
                    "Veículo desativado detectado",
                    "O veículo " + veiculo.getPlaca() + " tentou passar, mas está desativado."
            );

            emailService.sendGenericEmail(
                    pessoa.email(),
                    "Tentativa de passagem com veículo desativado",
                    "Seu veículo " + veiculo.getPlaca() + " tentou passar em " + dataFormatada + " às " + hora + ", mas está desativado."
            );

            throw new IllegalArgumentException("Veículo desativado");
        }

        var local = "Pedágio Modelo 1A";
        var valorPassagem = 24.50;

        Passagem passagem = new Passagem(
                null,
                veiculo.getIdVeiculo(),
                pessoa.id(),
                local,
                data,
                hora,
                valorPassagem
        );

        Passagem saved = repository.save(passagem);

        emailService.sendPassagemEmail(
                pessoa.email(),
                veiculo.getPlaca(),
                data,
                hora,
                valorPassagem
        );

        return saved;
    }

    public List<Passagem> listAll() {
        return repository.findAll();
    }

    public List<Passagem> listByCurrentPessoa(String idPessoa) {
        var mesAtual = java.time.YearMonth.now();
        var inicio = mesAtual.atDay(1);
        var fim = mesAtual.atEndOfMonth();
        return repository.findByPessoa(idPessoa);
    }

    public Map<String, Object> listByCurrentPessoaWithPeriod(String idPessoa, LocalDate dataInicio, LocalDate dataFim) {
        LocalDate hoje = LocalDate.now();

        if (dataInicio.isAfter(hoje) || dataFim.isAfter(hoje)) {
            throw new IllegalArgumentException("Não é permitido filtrar datas futuras");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data início não pode ser maior que data fim");
        }

        List<Passagem> passagens = repository.findByPessoaAndPeriodo(idPessoa, dataInicio, dataFim);

        boolean mesFechado = false;
        YearMonth periodoInicio = YearMonth.from(dataInicio);
        YearMonth periodoFim = YearMonth.from(dataFim);

        if (periodoInicio.equals(periodoFim) &&
                dataInicio.getDayOfMonth() == 1 &&
                dataFim.getDayOfMonth() == periodoFim.lengthOfMonth()) {
            mesFechado = true;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mesFechado", mesFechado);
        response.put("passagens", passagens);

        return response;
    }

    public String findPessoaIdByEmail(String email) {
        Pessoa p = pessoaService.findByEmail(email);
        return p.id();
    }
}
