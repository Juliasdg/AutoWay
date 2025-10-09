export interface Passagem {
  idPassagem: string;
  idVeiculo: string;
  data: string;
  hora: string;
  local: string;
  valor: number;
  placa?: string;
  mesFechado?: boolean;
  boletoId?: string;
}
