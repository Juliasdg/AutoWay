package br.com.fatec.autoway.infra.orm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "passagem")
@Getter @Setter
public class PassagemOrm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_passagem")
    private Integer id;

    @Column(name = "id_veiculo", nullable = false, length = 36)
    private String idVeiculo;

    @Column(name = "id_pessoa", nullable = false, length = 36)
    private String idPessoa;

    @Column(name = "local", nullable = false)
    private String local;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "valor", nullable = false)
    private Double valor;

    public PassagemOrm() {}

    public PassagemOrm(Integer id, String idVeiculo, String idPessoa, String local, LocalDate data, LocalTime hora, Double valor) {
        this.id = id;
        this.idVeiculo = idVeiculo;
        this.idPessoa = idPessoa;
        this.local = local;
        this.data = data;
        this.hora = hora;
        this.valor = valor;
    }
}
