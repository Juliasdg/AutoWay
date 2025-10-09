import { ApplicationConfig, NgModule, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { CarHistoryComponent } from './pages/car-history/car-history.component';
import { HomeComponent } from './pages/home/home.component';
import { ActiveCarComponent } from './pages/active-car/active-car.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { EditCarComponent } from './pages/edit-car/edit-car.component';
import { EditPasswordComponent } from './pages/edit-password/edit-password.component';
import { EditRegisterComponent } from './pages/edit-register/edit-register.component';
import { HomeAdminComponent } from './pages/home-admin/home-admin.component';
import { InativateCarComponent } from './pages/inativate-car/inativate-car.component';
import { NewCarComponent } from './pages/new-car/new-car.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { RegisterComponent } from './pages/register/register.component';
import { BrowserModule } from '@angular/platform-browser'
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { ReactiveFormsModule } from '@angular/forms';
import { provideNgxMask } from 'ngx-mask';
import { LoginComponent } from './pages/login/login.component';
import { AdminVehiclesComponent } from './pages/admin-vehicles/admin-vehicles.component';
import { AdminUsersComponent } from './pages/admin-users/admin-users.component';
import { InativateAccountComponent } from './pages/inativate-account/inativate-account.component';
import { BoletoHistoryComponent } from './pages/boleto-history/boleto-history.component';
import { AdminPassagensComponent } from './pages/admin-passagens/admin-passagens.component';
import { AdminBoletoComponent } from './pages/admin-boleto/admin-boleto.component';
import { AdminEditRegistersComponent } from './pages/admin-edit-registers/admin-edit-registers.component';



NgModule({
  declarations: [
    HomeComponent,
    LoginComponent,
    AdminVehiclesComponent,
    AdminUsersComponent,
    ActiveCarComponent,
    InativateAccountComponent,
    CarHistoryComponent,
    BoletoHistoryComponent,
    AdminPassagensComponent,
    ForgotPasswordComponent,
    AdminEditRegistersComponent,
    EditCarComponent,
    EditPasswordComponent,
    AdminBoletoComponent,
    EditRegisterComponent,
    HomeComponent,
    HomeAdminComponent,
    InativateCarComponent,
    NewCarComponent,
    ProfileComponent,
    RegisterComponent,
  ],
  imports: [
    BrowserModule, MatDatepickerModule, MatInputModule, MatNativeDateModule, ReactiveFormsModule, provideNgxMask()
  ]
})


export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes), provideNgxMask(), { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ]

};
