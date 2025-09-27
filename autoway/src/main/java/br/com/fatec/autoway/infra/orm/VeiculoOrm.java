package br.com.fatec.autoway.infra.orm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "veiculo", uniqueConstraints = {
        @UniqueConstraint(columnNames = "placa"),
        @UniqueConstraint(columnNames = "id_rfid")
})
@Getter @Setter
public class VeiculoOrm {

    @Id
    @Column(name = "id_veiculo", columnDefinition = "CHAR(36)")
    private String idVeiculo;

    @Column(name = "id_pessoa", nullable = false, columnDefinition = "CHAR(36)")
    private String idPessoa;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(name = "id_rfid", unique = true)
    private String rfid;

    @Column(nullable = false)
    private boolean ativo = false;
}
