import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header-purple',
  standalone: true,
  imports: [CommonModule], // 👈 adiciona aqui
  templateUrl: './header-purple.component.html',
  styleUrls: ['./header-purple.component.scss']
})
export class HeaderPurpleComponent {
  dropdownOpen = false;
  isAdmin = false;

  constructor(
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.checkAdmin();
  }

  private checkAdmin() {
    this.isAdmin = this.authService.getUserRole() === 'admin';
  }

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

  onBoletos() {
    if (this.isAdmin) {
      this.router.navigate(['/manage/boletos']);
    } else {
      this.router.navigate(['/boletos']);
    }
  }

  onHistoric() {
    if (this.isAdmin) {
      this.router.navigate(['/manage/passagens']);
    } else {
      this.router.navigate(['/passagens']);
    }

  }

  goHome() {
    if (this.isAdmin) {
      this.router.navigate(['/home-admin']);
    } else {
      this.router.navigate(['/']);
    }
  }

  onUsers() {
    this.router.navigate(['/manage/users']);
  }

  onVehicles() {
    this.router.navigate(['/manage/vehicles']);
  }
}
