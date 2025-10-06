import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  dropdownOpen = false;
    isAdmin = false;



  constructor(
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit() {
    this.checkAdmin();
  }

  private checkAdmin() {
      this.isAdmin = this.authService.getUserRole() === 'admin';
  }


  onLogout() {
    const userId = this.authService.getUserId();
    if (!userId) return;

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

  toggleDropdown(event: Event) {
    event.stopPropagation(); // impede o clique de propagar e fechar imediatamente
    this.dropdownOpen = !this.dropdownOpen;
  }

   goHome() {
    if (this.isAdmin) {
      this.router.navigate(['/home-admin']);
    } else {
      this.router.navigate(['/']);
    }
  }

  @HostListener('document:click')
  closeDropdown() {
    this.dropdownOpen = false;
  }

  
    onProfile() {
      this.router.navigate(['/profile']);
    }
}
