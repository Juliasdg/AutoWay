export interface Passagem {
  idVeiculo: string;
  idPessoa: string;
  local: string;
  data: string;
  hora: string;
  valor: number;
  placa?: string; // enriquecido depois
}