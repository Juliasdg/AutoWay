package br.com.fatec.autoway.infra.orm;

import jakarta.persistence.*;
import br.com.fatec.autoway.domain.model.TipoUsuario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa")
@Getter @Setter
public class PessoaOrm {

    // getters e setters
    @Id
    @Column(name = "id_pessoa", length = 36)
    private String id;

    @Column(name = "nome_pessoa", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 10)
    private TipoUsuario tipoUsuario;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "cpf", length = 14, unique = true)
    private String cpf;

    @Column(name = "cep", length = 10)
    private String cep;

    @Column(name = "endereco", length = 255)
    private String endereco;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name= "vencimento")
    private Integer vencimento;


    public PessoaOrm() {}

    public PessoaOrm(String id, String nome, String email, String senhaHash, TipoUsuario tipoUsuario, Boolean status,
                     String telefone, String cpf, String cep, String endereco, String complemento, LocalDate dataNascimento, Integer vencimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipoUsuario = tipoUsuario;
        this.status = status;
        this.telefone = telefone;
        this.cpf = cpf;
        this.cep = cep;
        this.endereco = endereco;
        this.complemento = complemento;
        this.dataNascimento = dataNascimento;
        this.vencimento = vencimento;
    }

}
