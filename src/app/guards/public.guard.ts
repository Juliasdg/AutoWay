import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class PublicGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    // Se já estiver autenticado, redireciona para a home correspondente
    if (this.auth.isAuthenticated()) {
      const role = this.auth.getUserRole();
      if (role === 'admin') {
        this.router.navigate(['/home-admin']);
      } else {
        this.router.navigate(['/']);
      }
      return false;
    }

    return true; // se não estiver logado, deixa entrar
  }
}
