package br.com.fatec.autoway.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Veiculo {
    private String idVeiculo;
    private String idPessoa;
    private String placa;
    private String idRfid;
    private boolean ativo;

    public Veiculo(String idVeiculo, String idPessoa, String placa, String idRfid, boolean ativo) {
        this.idVeiculo = idVeiculo;
        this.idPessoa = idPessoa;
        this.placa = placa;
        this.idRfid = idRfid;
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "id='" + idVeiculo + '\'' +
                ", idPessoa='" + idPessoa + '\'' +
                ", placa='" + placa + '\'' +
                ", idRfid='" + idRfid + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
