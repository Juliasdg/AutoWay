export interface VeiculoResponse {
  idVeiculo: string;
  idPessoa: string;
  placa: string;
  idRfid: string | null;
  ativo: boolean;
  portadorNome?: string;
}