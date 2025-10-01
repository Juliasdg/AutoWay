package br.com.fatec.autoway.infra.repository.adapter;

import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.domain.port.PessoaRepositoryPort;
import br.com.fatec.autoway.infra.orm.PessoaOrm;
import br.com.fatec.autoway.infra.repository.jpa.PessoaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PessoaRepositoryAdapter implements PessoaRepositoryPort {

    private final PessoaRepository jpa;

    public PessoaRepositoryAdapter(PessoaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        PessoaOrm orm = toOrm(pessoa);
        PessoaOrm saved = jpa.save(orm);
        return toDomain(saved);
    }

    @Override
    public Optional<Pessoa> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Pessoa> findByEmail(String email) {
        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Pessoa> findByNome(String nome) {
        return jpa.findByNome(nome).map(this::toDomain);
    }

    @Override
    public Optional<Pessoa> findByCpf(String cpf) {
        return jpa.findByCpf(cpf).map(this::toDomain);
    }

    @Override
    public List<Pessoa> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String id, Boolean status) {
        jpa.findById(id).ifPresent(orm -> {
            orm.setStatus(status);
            jpa.save(orm);
        });
    }

    private Pessoa toDomain(PessoaOrm orm) {
        return new Pessoa(
                orm.getId(),
                orm.getNome(),
                orm.getEmail(),
                orm.getSenhaHash(),
                orm.getTipoUsuario(),
                orm.getStatus(),
                orm.getTelefone(),
                orm.getCpf(),
                orm.getCep(),
                orm.getEndereco(),
                orm.getComplemento(),
                orm.getDataNascimento(),
                orm.getVencimento()
        );
    }

    private PessoaOrm toOrm(Pessoa p) {
        return new PessoaOrm(
                p.id(),
                p.nome(),
                p.email(),
                p.senhaHash(),
                p.tipoUsuario(),
                p.status(),
                p.telefone(),
                p.cpf(),
                p.cep(),
                p.endereco(),
                p.complemento(),
                p.dataNascimento(),
                p.vencimento()
        );
    }
}
