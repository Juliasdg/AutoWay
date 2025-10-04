import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { HomeComponent } from './pages/home/home.component';
import { HomeAdminComponent } from './pages/home-admin/home-admin.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { EditPasswordComponent } from './pages/edit-password/edit-password.component';
import { EditRegisterComponent } from './pages/edit-register/edit-register.component';
import { CarHistoryComponent } from './pages/car-history/car-history.component';
import { RegisteredCarComponent } from './pages/registered-car/registered-car.component';
import { ActiveCarComponent } from './pages/active-car/active-car.component';
import { EditCarComponent } from './pages/edit-car/edit-car.component';
import { InativateCarComponent } from './pages/inativate-car/inativate-car.component';
import { ListUsersComponent } from './pages/list-users/list-users.component';
import { NewCarComponent } from './pages/new-car/new-car.component';

export const routes: Routes = [
  // rotas públicas para quem não está logado
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard], data: { requiresAuth: false } },
  { path: 'register', component: RegisterComponent, canActivate: [AuthGuard], data: { requiresAuth: false } },
  { path: 'forgot-password', component: ForgotPasswordComponent, canActivate: [AuthGuard], data: { requiresAuth: false } },

  // rotas protegidas, só para usuários logados
  { path: '', component: HomeComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'home-admin', component: HomeAdminComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'edit-password', component: EditPasswordComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'edit-register', component: EditRegisterComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'car-history', component: CarHistoryComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'registered', component: RegisteredCarComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'activate-car', component: ActiveCarComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'edit-car', component: EditCarComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'inactivate-car', component: InativateCarComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'list-users', component: ListUsersComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },
  { path: 'new-car', component: NewCarComponent, canActivate: [AuthGuard], data: { requiresAuth: true } },

  // fallback
  { path: '**', redirectTo: '' }
];
