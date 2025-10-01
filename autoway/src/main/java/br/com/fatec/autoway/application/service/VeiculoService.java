package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.domain.model.Veiculo;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.domain.port.persistence.VeiculoRepositoryPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class VeiculoService {

    private final VeiculoRepositoryPort repository;
    private final EmailService emailService;
    private final PessoaService pessoaService;

    public VeiculoService(VeiculoRepositoryPort repository, EmailService emailService, PessoaService pessoaService) {
        this.repository = repository;
        this.emailService = emailService;
        this.pessoaService = pessoaService;
    }

    public String findPessoaIdByEmail(String email) {
        Pessoa p = pessoaService.findByEmail(email);
        return p.id();
    }

    public Optional<Veiculo> findById(String idVeiculo) {
        return repository.findById(idVeiculo);
    }


    public Veiculo createVeiculo(String idPessoa, String placa) {
        if (!isValidPlaca(placa)) throw new IllegalArgumentException("Placa inválida");
        if (repository.findByPlaca(placa).isPresent()) throw new IllegalArgumentException("Placa já registrada");

        Veiculo v = new Veiculo(UUID.randomUUID().toString(), idPessoa, placa, null, false);
        repository.save(v);

        Pessoa pessoa = pessoaService.findById(idPessoa); // pega o objeto completo
        String clienteEmail = pessoa.email();
        String clienteNome = pessoa.nome(); // supondo que você tenha esse método

        emailService.sendGenericEmail(clienteEmail,
                "Veículo aguardando validação",
                "Olá " + clienteNome + ", seu veículo com placa " + placa + " está aguardando validação pelo admin.");

        notifyAdminsNewVehicle(v, clienteNome);

        return v;
    }

    public Veiculo assignRfid(String idVeiculo, String idRfid) {
        if (repository.findByRfid(idRfid).isPresent()) throw new IllegalArgumentException("RFID já cadastrado");

        Veiculo v = repository.findById(idVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        v.setIdRfid(idRfid);
        v.setAtivo(true);
        repository.save(v);

        String clienteEmail = pessoaService.findById(v.getIdPessoa()).email();
        emailService.sendGenericEmail(clienteEmail,
                "Veículo ativado",
                "Seu veículo com placa " + v.getPlaca() + " foi validado e está ativo!");

        return v;
    }

    public List<Veiculo> search(String placa, String rfid) {
        return repository.findAll().stream()
                .filter(v -> placa == null || v.getPlaca().toLowerCase().contains(placa.toLowerCase()))
                .filter(v -> rfid == null || (v.getIdRfid() != null && v.getIdRfid().equalsIgnoreCase(rfid)))
                .toList();
    }


    public List<Veiculo> listByClient(String idPessoa) {
        return repository.findByPessoa(idPessoa);
    }

    public List<Veiculo> listAll() {
        return repository.findAll();
    }

    public void inactivate(String idVeiculo, String idPessoa, boolean isAdmin) {
        Veiculo v = repository.findById(idVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (!isAdmin && !v.getIdPessoa().equals(idPessoa)) {
            throw new IllegalArgumentException("Ação não permitida");
        }

        v.setAtivo(false);
        repository.save(v);
    }

    public void reactivate(String idVeiculo, String idPessoa, boolean isAdmin) {
        Veiculo v = repository.findById(idVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (!isAdmin && !v.getIdPessoa().equals(idPessoa)) {
            throw new IllegalArgumentException("Ação não permitida");
        }

        v.setAtivo(true);
        repository.save(v);
    }

    private boolean isValidPlaca(String placa) {
        String regexAntigo = "^[A-Z]{3}\\d{4}$";
        String regexMercosul = "^[A-Z]{3}\\d[A-Z]\\d{2}$";
        return Pattern.compile(regexAntigo).matcher(placa).matches() ||
                Pattern.compile(regexMercosul).matcher(placa).matches();
    }

    public Veiculo findByRfid(String rfid) {
        return repository.findByRfid(rfid)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado para RFID: " + rfid));
    }

    public List<Veiculo> findAllAtivos() {
        return repository.findAll().stream()
                .filter(Veiculo::isAtivo)
                .toList();
    }


    @Async
    void notifyAdminsNewVehicle(Veiculo v, String clienteNome) {
        List<String> admins = repository.listAdminEmails();
        for (String adminEmail : admins) {
            emailService.sendGenericEmail(adminEmail,
                    "Novo veículo aguardando validação",
                    "O cliente " + clienteNome + " cadastrou o veículo com placa " + v.getPlaca());
        }
    }
}
