export interface PessoaResponse {
  id: string;
  nome: string;
  email: string;
  tipoUsuario: string;
  status: boolean;
  telefone: string;
  cpf: string;
  cep: string;
  endereco: string;
  complemento: string;
  dataNascimento?: string;
  vencimento: number;
}