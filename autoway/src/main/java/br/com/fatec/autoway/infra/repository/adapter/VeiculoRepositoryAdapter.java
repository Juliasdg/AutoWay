package br.com.fatec.autoway.infra.repository.adapter;

import br.com.fatec.autoway.domain.model.Veiculo;
import br.com.fatec.autoway.domain.port.VeiculoRepositoryPort;
import br.com.fatec.autoway.infra.orm.VeiculoOrm;
import br.com.fatec.autoway.domain.port.PessoaRepositoryPort;
import br.com.fatec.autoway.domain.model.Pessoa;
import br.com.fatec.autoway.infra.repository.jpa.VeiculoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepositoryPort {

    private final VeiculoRepository jpa;
    private final PessoaRepositoryPort pessoaRepositoryPort;

    public VeiculoRepositoryAdapter(VeiculoRepository jpa, PessoaRepositoryPort pessoaRepositoryPort) {
        this.jpa = jpa;
        this.pessoaRepositoryPort = pessoaRepositoryPort;
    }

    private Veiculo toDomain(VeiculoOrm orm) {
        if (orm == null) return null;
        return new Veiculo(
                orm.getIdVeiculo(),
                orm.getIdPessoa(),
                orm.getPlaca(),
                orm.getRfid(),
                orm.isAtivo()
        );
    }

    private VeiculoOrm toOrm(Veiculo v) {
        VeiculoOrm orm = new VeiculoOrm();
        if (v.getIdVeiculo() == null) {
            orm.setIdVeiculo(UUID.randomUUID().toString());
            v.setIdVeiculo(orm.getIdVeiculo());
        } else {
            orm.setIdVeiculo(v.getIdVeiculo());
        }
        orm.setIdPessoa(v.getIdPessoa());
        orm.setPlaca(v.getPlaca());
        orm.setRfid(v.getIdRfid());
        orm.setAtivo(v.isAtivo());
        return orm;
    }

    @Override
    public void save(Veiculo veiculo) {
        VeiculoOrm orm = toOrm(veiculo);
        VeiculoOrm saved = jpa.save(orm);
        veiculo.setIdVeiculo(saved.getIdVeiculo());
    }

    @Override
    public Optional<Veiculo> findById(String idVeiculo) {
        return jpa.findById(idVeiculo).map(this::toDomain);
    }

    @Override
    public List<Veiculo> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Veiculo> findByPessoa(String idPessoa) {
        return jpa.findByIdPessoa(idPessoa).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Veiculo> findByPlaca(String placa) {
        return jpa.findByPlaca(placa).map(this::toDomain);
    }

    @Override
    public Optional<Veiculo> findByRfid(String idRfid) {
        return jpa.findByRfid(idRfid).map(this::toDomain);
    }

    @Override
    public List<String> listAdminEmails() {
        return pessoaRepositoryPort.findAll().stream()
                .filter(p -> p.tipoUsuario().name().equalsIgnoreCase("ADMIN"))
                .map(Pessoa::email)
                .collect(Collectors.toList());
    }
}
