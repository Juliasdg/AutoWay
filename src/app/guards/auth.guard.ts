import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    const requiresAuth = route.data['requiresAuth'] ?? true;
    const allowedRoles: string[] = route.data['roles'] ?? []; // Roles permitidas para a rota

    // Se precisa de autenticação e não está logado, redireciona
    if (requiresAuth && !this.auth.isAuthenticated()) {
      return this.router.createUrlTree(['/login']);
    }

    // Se existem roles definidas, verifica se o usuário possui uma delas
    if (allowedRoles.length > 0) {
      const userRole = this.auth.getUserRole();
      if (!userRole || !allowedRoles.includes(userRole)) {
        // Redireciona para página padrão ou de acesso negado
        return this.router.createUrlTree(['/']);
      }
    }

    return true;
  }
}
