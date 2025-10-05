import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-header-purple',
  standalone: true,
  templateUrl: './header-purple.component.html',
  styleUrls: ['./header-purple.component.scss']
})
export class HeaderPurpleComponent {
  dropdownOpen = false;

  constructor(
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) {}

  toggleDropdown(event: Event) {
    event.stopPropagation(); // impede o clique de propagar e fechar imediatamente
    this.dropdownOpen = !this.dropdownOpen;
  }

  @HostListener('document:click')
  closeDropdown() {
    this.dropdownOpen = false;
  }

  onLogout() {
    this.authService.logout().subscribe({
      next: () => {
        this.alertService.success('Logout realizado com sucesso!', 'Até logo!');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.alertService.httpError(error.status, error, 'Erro ao sair!');
      }
    });
  }

    onProfile() {
      this.router.navigate(['/profile']);
    }
}
