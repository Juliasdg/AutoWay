import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class PublicGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) { }

  canActivate(): boolean {
    if (this.auth.isAuthenticated()) {
      const role = this.auth.getUserRole();
      if (role === 'admin') {
        this.router.navigate(['/home-admin']);
      } else {
        this.router.navigate(['/']);
      }
      return false;
    }

    return true;
  }
}
