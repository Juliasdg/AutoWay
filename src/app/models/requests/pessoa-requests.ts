export interface PessoaRequest {
  nome: string;
  email: string;
  senha: string;
  telefone: string;
  cpf: string;
  cep: string;
  endereco: string;
  complemento: string;
  dataNascimento: string;
  vencimento: number;
}

export interface PessoaUpdateRequest {
  nome: string;
  telefone: string;
  cep: string;
  endereco: string;
  complemento: string;
  dataNascimento: string;
  vencimento: number;
}