package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.application.util.DateUtils;
import br.com.fatec.autoway.domain.model.ConfirmationToken;
import br.com.fatec.autoway.domain.model.PasswordResetToken;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.domain.model.TipoUsuario;
import br.com.fatec.autoway.domain.port.persistence.PessoaRepositoryPort;
import br.com.fatec.autoway.infra.repository.ConfirmationTokenRepository;
import br.com.fatec.autoway.infra.repository.PasswordResetTokenRepository;
import br.com.fatec.autoway.web.dto.request.PessoaRequest;
import br.com.fatec.autoway.web.dto.request.PessoaUpdateRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class PessoaService {
    private final PessoaRepositoryPort repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;


    public PessoaService(PessoaRepositoryPort repository,
                         BCryptPasswordEncoder passwordEncoder,
                         ConfirmationTokenRepository tokenRepository,
                         PasswordResetTokenRepository resetTokenRepository,
                         EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
    }

    private String getLocalIp() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "localhost"; // fallback
        }
    }

    @Async
    public void initiatePasswordReset(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String token = String.format("%06d", new Random().nextInt(999999)); // 6 dígitos
        PasswordResetToken resetToken = new PasswordResetToken(token, user.id(),
                LocalDateTime.now().plusMinutes(15), false);

        resetTokenRepository.save(resetToken);

        // chama o novo método específico
        emailService.sendPasswordResetEmail(user.email(), user.nome(), token);
    }

    public void updatePassword(String email, String currentPassword, String newPassword) {
        Pessoa user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 1️⃣ Valida a senha atual
        if (!checkPassword(currentPassword, user.senhaHash())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        // 2️⃣ Verifica se a nova senha é igual à atual
        if (checkPassword(newPassword, user.senhaHash())) {
            throw new IllegalArgumentException("Nova senha não pode ser igual à senha atual");
        }

        // 3️⃣ Valida a nova senha quanto aos critérios (tamanho, caracteres, etc.)
        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Nova senha inválida. Deve ter mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais.");
        }

        // Atualiza a senha
        Pessoa updated = new Pessoa(
                user.id(),
                user.nome(),
                user.email(),
                passwordEncoder.encode(newPassword),
                user.tipoUsuario(),
                user.status(),
                user.telefone(),
                user.cpf(),
                user.cep(),
                user.endereco(),
                user.complemento(),
                user.dataNascimento(),
                user.vencimento()
        );

        repository.save(updated);
    }

    public boolean verifyResetCode(String email, String code) {
        try {
            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            var token = resetTokenRepository.findByUserIdAndToken(user.id(), code)
                    .orElseThrow(() -> new IllegalArgumentException("Código inválido"));

            if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Código expirado ou já usado");
            }

            return true;
        } catch (IllegalArgumentException ex) {
            // repassa a mensagem para o handler
            throw ex;
        } catch (Exception ex) {
            // log detalhado para depuração
            ex.printStackTrace();
            throw new IllegalArgumentException("Erro ao verificar o código de reset");
        }
    }

    public void resetPassword(String email, String code, String newPassword) {
        try {
            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            var token = resetTokenRepository.findByUserIdAndToken(user.id(), code)
                    .orElseThrow(() -> new IllegalArgumentException("Código inválido"));

            if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Código expirado ou já usado");
            }

            if (!isValidPassword(newPassword)) {
                throw new IllegalArgumentException(
                        "Senha inválida. Deve ter mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais."
                );
            }

            Pessoa updated = new Pessoa(
                    user.id(),
                    user.nome(),
                    user.email(),
                    passwordEncoder.encode(newPassword),
                    user.tipoUsuario(),
                    user.status(),
                    user.telefone(),
                    user.cpf(),
                    user.cep(),
                    user.endereco(),
                    user.complemento(),
                    user.dataNascimento(),
                    user.vencimento()
            );

            repository.save(updated);

            token.setUsed(true);
            resetTokenRepository.save(token);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Erro ao resetar a senha");
        }
    }

    public Pessoa create(PessoaRequest request, String role) {
        validatePessoaRequest(request);

        if (repository.findByEmail(request.email()).isPresent())
            throw new IllegalArgumentException("Email já registrado");

        String id = UUID.randomUUID().toString();
        String hashed = passwordEncoder.encode(request.senha());
        TipoUsuario tipo = "admin".equalsIgnoreCase(role) ? TipoUsuario.admin : TipoUsuario.cliente;

        LocalDate dataNascimento = DateUtils.parseDate(request.dataNascimento());
        DateUtils.validateAdult(dataNascimento);

        Pessoa p = new Pessoa(
                id,
                request.nome(),
                request.email(),
                hashed,
                tipo,
                false, // usuário começa inativo até confirmar email
                request.telefone(),
                request.cpf(),
                request.cep(),
                request.endereco(),
                request.complemento(),
                dataNascimento,
                request.vencimento()
        );

        repository.save(p);


        // criar token de confirmação
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                p.id(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(24)
        );
        tokenRepository.save(confirmationToken);

        // enviar email
        String ip = getLocalIp();
        String link = "http://" + ip + ":9000/pessoas/confirm?token=" + token;
        emailService.sendHtmlConfirmationEmail(p.email(), p.nome(), link);

        return p;
    }

    public String confirmUser(String token) {
        ConfirmationToken t = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (t.isConfirmed()) return "Email já confirmado.";
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) return "Token expirado.";

        // Usa apenas o ID do token para ativar o usuário
        repository.updateStatus(t.getUserId(), true);

        t.setConfirmed(true);
        tokenRepository.save(t);

        return "Email confirmado com sucesso!";
    }

    public void validatePessoaRequest(PessoaRequest req) {
        // Validação de email
        if (!isValidEmail(req.email())) {
            throw new IllegalArgumentException("Email inválido");
        }

        // Verifica duplicidade de email
        if (repository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email já registrado");
        }

        // Validação de CPF (se fornecido)
        if (req.cpf() != null) {
            if (!isValidCPF(req.cpf())) {
                throw new IllegalArgumentException("CPF inválido");
            }
            // Verifica duplicidade de CPF
            boolean cpfExists = repository.findAll().stream()
                    .anyMatch(p -> req.cpf().equals(p.cpf()));
            if (cpfExists) {
                throw new IllegalArgumentException("CPF já registrado");
            }
        }

        // Validação de senha
        if (!isValidPassword(req.senha())) {
            throw new IllegalArgumentException(
                    "Senha inválida. Deve ter mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais."
            );
        }

        // Validação de idade
        if (req.dataNascimento() != null) {
            LocalDate dataNascimento = DateUtils.parseDate(req.dataNascimento());
            DateUtils.validateAdult(dataNascimento);
        }
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";
        return Pattern.compile(regex).matcher(password).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(email).matches();
    }


    public List<Pessoa> findAllAtivos() {
        return repository.findAll().stream()
                .filter(Pessoa::status) // considera apenas ativos
                .toList();
    }

    private boolean isValidCPF(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11) return false;
        // Check para todos os dígitos iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int sum1 = 0;
            for (int i = 0; i < 9; i++) sum1 += (cpf.charAt(i) - '0') * (10 - i);
            int check1 = (sum1 * 10) % 11;
            if (check1 == 10) check1 = 0;
            if (check1 != (cpf.charAt(9) - '0')) return false;

            int sum2 = 0;
            for (int i = 0; i < 10; i++) sum2 += (cpf.charAt(i) - '0') * (11 - i);
            int check2 = (sum2 * 10) % 11;
            if (check2 == 10) check2 = 0;
            return check2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    public Pessoa findById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
    }

    public List<Pessoa> listAll() {
        return repository.findAll();
    }

    public Pessoa findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
    }

    public Pessoa updateOwn(String email, PessoaUpdateRequest request) {
        Pessoa existing = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        LocalDate dataNascimento = request.dataNascimento() != null
                ? DateUtils.parseDate(request.dataNascimento())
                : existing.dataNascimento();
        DateUtils.validateAdult(dataNascimento);

        Pessoa updated = new Pessoa(
                existing.id(),
                request.nome() != null ? request.nome() : existing.nome(),
                existing.email(),
                existing.senhaHash(),
                existing.tipoUsuario(),
                existing.status(),
                request.telefone() != null ? request.telefone() : existing.telefone(),
                existing.cpf(),
                request.cep() != null ? request.cep() : existing.cep(),
                request.endereco() != null ? request.endereco() : existing.endereco(),
                request.complemento() != null ? request.complemento() : existing.complemento(),
                dataNascimento,
                request.vencimento() != null ? request.vencimento(): existing.vencimento()
        );

        return repository.save(updated);
    }

    public Pessoa updateAsAdmin(String id, PessoaUpdateRequest request) {
        Pessoa existing = findById(id);

        LocalDate dataNascimento = request.dataNascimento() != null
                ? DateUtils.parseDate(request.dataNascimento())
                : existing.dataNascimento();
        DateUtils.validateAdult(dataNascimento);

        Pessoa updated = new Pessoa(
                existing.id(),
                request.nome() != null ? request.nome() : existing.nome(),
                existing.email(),
                existing.senhaHash(),
                existing.tipoUsuario(),
                existing.status(),
                request.telefone() != null ? request.telefone() : existing.telefone(),
                existing.cpf(),
                request.cep() != null ? request.cep() : existing.cep(),
                request.endereco() != null ? request.endereco() : existing.endereco(),
                request.complemento() != null ? request.complemento() : existing.complemento(),
                dataNascimento,
                request.vencimento() != null ? request.vencimento() : existing.vencimento()
        );
        return repository.save(updated);
    }

    public void inactivate(String id) {
        repository.updateStatus(id, false);
    }

    public void reactivate(String id) {
        repository.updateStatus(id, true);
    }

    public boolean checkPassword(String raw, String hashed) {
        return passwordEncoder.matches(raw, hashed);
    }
}
