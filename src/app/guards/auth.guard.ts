import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiresAuth = route.data['requiresAuth'] as boolean;

    if (requiresAuth && !this.authService.isAuthenticated()) {
      // rota protegida, usuário não logado
      this.router.navigate(['/login']);
      return false;
    }

    if (!requiresAuth && this.authService.isAuthenticated()) {
      // rota só para não logados, mas usuário já está logado
      this.router.navigate(['']); // manda para home
      return false;
    }

    return true;
  }
}
