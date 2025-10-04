import { Injectable } from '@angular/core';
import Swal, { SweetAlertIcon } from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor() {}

  success(title: string = 'Sucesso', message: string) {
    console.log(title, message);
    this.showAlert('success', title, message);
  }

  error(title: string = 'Erro', error: Error) {
    console.error(title, error);
    this.showAlert('error', title, error.message);
  }

  httpError(statusCode: number = 999, error: Error, titleOverride?: string) {
    let message;
    let title;

    switch (statusCode) {
      case 0:
        return this.error(titleOverride, error);

      case 400:
        title = 'Erro de Validação!';
        message = 'Verifique os dados enviados.';
        break;

      case 401:
        title = 'Credenciais Inválidas!';
        message = 'Credenciais inválidas. Verifique seus dados.';
        break;

      case 403:
        title = 'Acesso Negado!';
        message = 'Você não tem permissão para acessar este recurso.';
        break;

      case 404:
        title = 'Recurso Não Encontrado!';
        message = 'Recurso não encontrado.';
        break;

      case 500:
        title = 'Erro no Servidor!';
        message = 'Tente novamente mais tarde.';
        break;

      default:
        title = 'Erro Desconhecido!';
        message = 'Tente novamente.';
        break;
    }

    console.error('HTTP Error', { statusCode, error });
    this.showAlert('error', title, message);
  }

  warning(message: string, title: string = 'Aviso') {
    console.warn(title, message);
    this.showAlert('warning', title, message);
  }

  info(message: string, title: string = 'Informação') {
    this.showAlert('info', title, message);
  }

  confirm(message: string, title: string = 'Confirmação'): Promise<boolean> {
    return Swal.fire({
      title,
      text: message,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sim',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33'
    }).then(result => result.isConfirmed);
  }

  private showAlert(icon: SweetAlertIcon, title: string, message: string) {
    Swal.fire({
      icon,
      title,
      text: message,
      confirmButtonColor: '#6a2fa0'
    });
  }
}
