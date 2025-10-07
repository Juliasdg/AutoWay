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
import { InativateAccountComponent } from './pages/inativate-account/inativate-account.component';
import { BoletoHistoryComponent } from './pages/boleto-history/boleto-history.component';
import { AdminPassagensComponent } from './pages/admin-passagens/admin-passagens.component';
import { AdminBoletoComponent } from './pages/admin-boleto/admin-boleto.component';
import { AdminEditRegistersComponent } from './pages/admin-edit-registers/admin-edit-registers.component';

export const routes: Routes = [
  // rotas públicas para quem não está logado
  { path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent},
  { path: 'forgot-password', component: ForgotPasswordComponent},

  // rotas protegidas, só para usuários logados
  { path: '', component: HomeComponent, canActivate: [AuthGuard], data: { roles: ['cliente'] } },
  { path: 'home-admin', component: HomeAdminComponent, canActivate: [AuthGuard], data: { roles: ['admin'] } },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'profile/edit/password', component: EditPasswordComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'profile/edit', component: EditRegisterComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'passagens', component: CarHistoryComponent, canActivate: [AuthGuard], data: { roles: ['cliente'] } },
  { path: 'manage/passagens', component: AdminPassagensComponent, canActivate: [AuthGuard], data: { roles: ['admin'] } },
  { path: 'manage/boletos', component: AdminBoletoComponent, canActivate: [AuthGuard], data: { roles: ['admin'] } },
  { path: 'boletos', component: BoletoHistoryComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'registered', component: RegisteredCarComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'activate-car', component: ActiveCarComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'edit-car', component: EditCarComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'inactivate-car', component: InativateCarComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'profile/inactivate-account', component: InativateAccountComponent, canActivate: [AuthGuard], data: { roles: ['cliente', 'admin'] } },
  { path: 'list-users', component: ListUsersComponent, canActivate: [AuthGuard], data: { roles: ['admin'] } },
  { path: 'manage/users/edit/:id', component: AdminEditRegistersComponent, canActivate: [AuthGuard], data: { roles: ['admin'] } },
  { path: 'new-car', component: NewCarComponent, canActivate: [AuthGuard], data: { roles: ['cliente'] } },

  // fallback
  { path: '**', redirectTo: '' }
];
