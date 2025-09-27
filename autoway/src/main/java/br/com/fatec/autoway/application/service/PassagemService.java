package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.domain.model.Passagem;
import br.com.fatec.autoway.domain.port.persistence.PassagemRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
        var veiculo = veiculoService.findByRfid(rfid);
        var pessoa = pessoaService.findById(veiculo.getIdPessoa());

        var local = "No cu do Jeferson";
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
                data.toString(),
                hora.toString(),
                valorPassagem
        );

        return saved;
    }

    public List<Passagem> listAll() {
        return repository.findAll();
    }
}
